// <auto-generated/>

#nullable disable

using System;
using System.ClientModel;
using System.ClientModel.Primitives;
using System.Text.Json;

namespace _Type.Property.Optional.Models
{
    public partial class RequiredAndOptionalProperty : IJsonModel<RequiredAndOptionalProperty>
    {
        void IJsonModel<RequiredAndOptionalProperty>.Write(Utf8JsonWriter writer, ModelReaderWriterOptions options) => throw null;

        protected virtual void JsonModelWriteCore(Utf8JsonWriter writer, ModelReaderWriterOptions options) => throw null;

        RequiredAndOptionalProperty IJsonModel<RequiredAndOptionalProperty>.Create(ref Utf8JsonReader reader, ModelReaderWriterOptions options) => throw null;

        protected virtual RequiredAndOptionalProperty JsonModelCreateCore(ref Utf8JsonReader reader, ModelReaderWriterOptions options) => throw null;

        BinaryData IPersistableModel<RequiredAndOptionalProperty>.Write(ModelReaderWriterOptions options) => throw null;

        protected virtual BinaryData PersistableModelWriteCore(ModelReaderWriterOptions options) => throw null;

        RequiredAndOptionalProperty IPersistableModel<RequiredAndOptionalProperty>.Create(BinaryData data, ModelReaderWriterOptions options) => throw null;

        protected virtual RequiredAndOptionalProperty PersistableModelCreateCore(BinaryData data, ModelReaderWriterOptions options) => throw null;

        string IPersistableModel<RequiredAndOptionalProperty>.GetFormatFromOptions(ModelReaderWriterOptions options) => throw null;

        public static implicit operator BinaryContent(RequiredAndOptionalProperty requiredAndOptionalProperty) => throw null;

        public static explicit operator RequiredAndOptionalProperty(ClientResult result) => throw null;
    }
}