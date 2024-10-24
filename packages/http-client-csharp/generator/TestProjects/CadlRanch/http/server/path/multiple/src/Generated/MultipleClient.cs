// <auto-generated/>

#nullable disable

using System;
using System.ClientModel;
using System.ClientModel.Primitives;
using System.Threading.Tasks;

namespace Server.Path.Multiple
{
    public partial class MultipleClient
    {
        protected MultipleClient() => throw null;

        public MultipleClient(Uri endpoint) : this(endpoint, new MultipleClientOptions()) => throw null;

        public MultipleClient(Uri endpoint, MultipleClientOptions options) => throw null;

        public ClientPipeline Pipeline => throw null;

        public virtual ClientResult NoOperationParams(RequestOptions options) => throw null;

        public virtual Task<ClientResult> NoOperationParamsAsync(RequestOptions options) => throw null;

        public virtual ClientResult NoOperationParams() => throw null;

        public virtual Task<ClientResult> NoOperationParamsAsync() => throw null;

        public virtual ClientResult WithOperationPathParam(string keyword, RequestOptions options) => throw null;

        public virtual Task<ClientResult> WithOperationPathParamAsync(string keyword, RequestOptions options) => throw null;

        public virtual ClientResult WithOperationPathParam(string keyword) => throw null;

        public virtual Task<ClientResult> WithOperationPathParamAsync(string keyword) => throw null;
    }
}