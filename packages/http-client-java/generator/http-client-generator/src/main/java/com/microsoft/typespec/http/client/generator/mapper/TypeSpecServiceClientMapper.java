// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.typespec.http.client.generator.mapper;

import com.azure.core.util.CoreUtils;
import com.microsoft.typespec.http.client.generator.core.extension.model.codemodel.Client;
import com.microsoft.typespec.http.client.generator.core.extension.model.codemodel.CodeModel;
import com.microsoft.typespec.http.client.generator.core.extension.model.codemodel.OperationGroup;
import com.microsoft.typespec.http.client.generator.core.extension.model.codemodel.Parameter;
import com.microsoft.typespec.http.client.generator.core.mapper.Mappers;
import com.microsoft.typespec.http.client.generator.core.mapper.ServiceClientMapper;
import com.microsoft.typespec.http.client.generator.core.model.clientmodel.ClientAccessorMethod;
import com.microsoft.typespec.http.client.generator.core.model.clientmodel.EnumType;
import com.microsoft.typespec.http.client.generator.core.model.clientmodel.IType;
import com.microsoft.typespec.http.client.generator.core.model.clientmodel.MethodGroupClient;
import com.microsoft.typespec.http.client.generator.core.model.clientmodel.PipelinePolicyDetails;
import com.microsoft.typespec.http.client.generator.core.model.clientmodel.Proxy;
import com.microsoft.typespec.http.client.generator.core.model.clientmodel.ServiceClient;
import com.microsoft.typespec.http.client.generator.core.model.clientmodel.ServiceClientProperty;
import com.microsoft.typespec.http.client.generator.core.util.ClientModelUtil;
import com.microsoft.typespec.http.client.generator.core.util.SchemaUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TypeSpecServiceClientMapper extends ServiceClientMapper {

    private final Map<Client, ServiceClient> parsed = new ConcurrentHashMap<>();

    public ServiceClient map(Client client, CodeModel codeModel) {
        if (parsed.containsKey(client)) {
            return parsed.get(client);
        }

        ServiceClient.Builder builder = createClientBuilder();

        String baseName = SchemaUtil.getJavaName(client);
        String className = ClientModelUtil.getClientImplementClassName(baseName);
        String packageName = ClientModelUtil.getServiceClientPackageName(className);
        builder.interfaceName(baseName).className(className).packageName(packageName);
        if (client.getLanguage().getJava() != null
            && !CoreUtils.isNullOrEmpty(client.getLanguage().getJava().getNamespace())) {
            builder.builderPackageName(client.getLanguage().getJava().getNamespace());
        }

        builder.builderDisabled(!client.isBuildMethodPublic());

        Proxy proxy = null;
        OperationGroup clientOperationGroup = client.getOperationGroups()
            .stream()
            .filter(og -> CoreUtils.isNullOrEmpty(SchemaUtil.getJavaName(og)))
            .findFirst()
            .orElse(null);
        if (clientOperationGroup != null) {
            proxy = processClientOperations(builder, clientOperationGroup.getOperations(), baseName);
        } else {
            builder.clientMethods(Collections.emptyList());
        }

        List<ServiceClientProperty> properties = processClientProperties(client,
            client.getServiceVersion() == null ? null : client.getServiceVersion().getLanguage().getJava().getName());

        List<MethodGroupClient> methodGroupClients = new ArrayList<>();
        client.getOperationGroups()
            .stream()
            .filter(og -> !CoreUtils.isNullOrEmpty(SchemaUtil.getJavaName(og)))
            .forEach(og -> methodGroupClients.add(Mappers.getMethodGroupMapper().map(og, properties)));
        builder.methodGroupClients(methodGroupClients);

        if (proxy == null
            && CoreUtils.isNullOrEmpty(methodGroupClients)
            && CoreUtils.isNullOrEmpty(client.getSubClients())) {
            // No operation in this client, no operation group, no sub client as well. Abort the processing.
            return null;
        }

        if (proxy == null && !CoreUtils.isNullOrEmpty(methodGroupClients)) {
            proxy = methodGroupClients.iterator().next().getProxy();
        }

        // TODO (weidxu): security definition could be different for different client
        processParametersAndConstructors(builder, client, codeModel, properties, proxy);

        processPipelinePolicyDetails(builder, client);

        builder.crossLanguageDefinitionId(SchemaUtil.getCrossLanguageDefinitionId(client));

        List<ClientAccessorMethod> clientAccessorMethods = new ArrayList<>();
        for (Client subClient : client.getSubClients()) {
            if (subClient.isParentAccessorPublic()) {
                ServiceClient subServiceClient = this.map(subClient, codeModel);
                clientAccessorMethods.add(new ClientAccessorMethod(subServiceClient));
            }
        }
        builder.clientAccessorMethods(clientAccessorMethods);

        ServiceClient serviceClient = builder.build();

        clientAccessorMethods.forEach(m -> {
            m.setServiceClient(serviceClient);
            m.getSubClient().setParentClient(serviceClient);
        });

        parsed.put(client, serviceClient);
        return serviceClient;
    }

    private static void processPipelinePolicyDetails(ServiceClient.Builder builder, Client client) {
        // handle corner case of RequestIdPolicy with header name "client-request-id"
        final String clientRequestIdHeaderName = "client-request-id";
        final boolean clientRequestIdHeaderInClient = client.getOperationGroups()
            .stream()
            .flatMap(og -> og.getOperations().stream())
            .anyMatch(o -> o.getSpecialHeaders() != null && o.getSpecialHeaders().contains(clientRequestIdHeaderName));
        if (clientRequestIdHeaderInClient) {
            builder
                .pipelinePolicyDetails(new PipelinePolicyDetails().setRequestIdHeaderName(clientRequestIdHeaderName));
        }
    }

    @Override
    protected boolean isRemoveModelFromParameter(Parameter parameter, IType type) {
        boolean isEnumType = type instanceof EnumType;
        boolean isClientParameter = Parameter.ImplementationLocation.CLIENT.equals(parameter.getImplementation());
        return super.isRemoveModelFromParameter(parameter, type) && !(isEnumType && isClientParameter);
    }
}
