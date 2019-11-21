package com.microsoft.xbox.idp.telemetry.utc;

import com.microsoft.bond.BondDataType;
import com.microsoft.bond.BondMirror;
import com.microsoft.bond.BondSerializable;
import com.microsoft.bond.FieldDef;
import com.microsoft.bond.Metadata;
import com.microsoft.bond.Modifier;
import com.microsoft.bond.ProtocolCapability;
import com.microsoft.bond.ProtocolReader;
import com.microsoft.bond.ProtocolReader.FieldTag;
import com.microsoft.bond.ProtocolWriter;
import com.microsoft.bond.SchemaDef;
import com.microsoft.bond.StructDef;
import com.microsoft.bond.TypeDef;
import com.microsoft.bond.internal.Marshaler;
import com.microsoft.bond.internal.ReadHelper;

import java.io.IOException;
import java.io.InputStream;

import Microsoft.Telemetry.Data;

public class PageView extends Data<CommonData> {
    private String fromPage;
    private String pageName;

    public static class Schema {
        public static final Metadata fromPage_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final Metadata pageName_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("PageView");
            metadata.setQualified_name("com.microsoft.xbox.idp.telemetry.utc.PageView");
            metadata.getAttributes().put("Description", "OnlineId PageView event");
            pageName_metadata.setName("pageName");
            pageName_metadata.setModifier(Modifier.Required);
            pageName_metadata.getAttributes().put("Description", "The name of the currently viewed page");
            fromPage_metadata.setName("fromPage");
            fromPage_metadata.setModifier(Modifier.Required);
            fromPage_metadata.getAttributes().put("Description", "The name of the previously viewed page (aka Referer Page Uri)");
            schemaDef.setRoot(getTypeDef(schemaDef));
        }

        public static TypeDef getTypeDef(SchemaDef schema) {
            TypeDef type = new TypeDef();
            type.setId(BondDataType.BT_STRUCT);
            type.setStruct_def(getStructDef(schema));
            return type;
        }

        private static short getStructDef(SchemaDef schema) {
            short pos = 0;
            while (true) {
                if (pos >= schema.getStructs().size()) {
                    StructDef structDef = new StructDef();
                    schema.getStructs().add(structDef);
                    structDef.setMetadata(metadata);
                    structDef.setBase_def(Microsoft.Telemetry.Data.Schema.getTypeDef(schema));
                    FieldDef field = new FieldDef();
                    field.setId((short) 10);
                    field.setMetadata(pageName_metadata);
                    field.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId((short) 20);
                    field2.setMetadata(fromPage_metadata);
                    field2.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field2);
                    break;
                } else if (((StructDef) schema.getStructs().get(pos)).getMetadata() == metadata) {
                    break;
                } else {
                    pos = (short) (pos + 1);
                }
            }
            return pos;
        }
    }

    public BondSerializable clone() {
        return null;
    }

    public final String getPageName() {
        return this.pageName;
    }

    public final void setPageName(String value) {
        this.pageName = value;
    }

    public final String getFromPage() {
        return this.fromPage;
    }

    public final void setFromPage(String value) {
        this.fromPage = value;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case 10:
                return this.pageName;
            case 20:
                return this.fromPage;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.pageName = (String) value;
                return;
            case 20:
                this.fromPage = (String) value;
                return;
            default:
                return;
        }
    }

    public BondMirror createInstance(StructDef structDef) {
        return null;
    }

    public SchemaDef getSchema() {
        return getRuntimeSchema();
    }

    public static SchemaDef getRuntimeSchema() {
        return Schema.schemaDef;
    }

    public void reset() {
        reset("PageView", "com.microsoft.xbox.idp.telemetry.utc.PageView");
    }

    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.pageName = "";
        this.fromPage = "";
    }

    public void unmarshal(InputStream input) throws IOException {
        Marshaler.unmarshal(input, this);
    }

    public void unmarshal(InputStream input, BondSerializable schema) throws IOException {
        Marshaler.unmarshal(input, (SchemaDef) schema, this);
    }

    public void read(ProtocolReader reader) throws IOException {
        reader.readBegin();
        readNested(reader);
        reader.readEnd();
    }

    public void readNested(ProtocolReader reader) throws IOException {
        if (!reader.hasCapability(ProtocolCapability.TAGGED)) {
            readUntagged(reader, false);
        } else if (readTagged(reader, false)) {
            ReadHelper.skipPartialStruct(reader);
        }
    }

    public void read(ProtocolReader reader, BondSerializable schema) throws IOException {
    }

    public void readUntagged(ProtocolReader reader, boolean isBase) throws IOException {
        boolean canOmitFields = reader.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        reader.readStructBegin(isBase);
        super.readUntagged(reader, true);
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.pageName = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.fromPage = reader.readWString();
        }
        reader.readStructEnd();
    }

    public boolean readTagged(ProtocolReader reader, boolean isBase) throws IOException {
        FieldTag fieldTag;
        boolean isPartial = true;
        reader.readStructBegin(isBase);
        if (!super.readTagged(reader, true)) {
            return false;
        }
        while (true) {
            fieldTag = reader.readFieldBegin();
            if (fieldTag.type != BondDataType.BT_STOP && fieldTag.type != BondDataType.BT_STOP_BASE) {
                switch (fieldTag.id) {
                    case 10:
                        this.pageName = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.fromPage = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    default:
                        reader.skip(fieldTag.type);
                        break;
                }
                reader.readFieldEnd();
            }
        }
        /*if (fieldTag.type != BondDataType.BT_STOP_BASE) {
            isPartial = false;
        }
        reader.readStructEnd();
        return isPartial;*/
    }

    public void marshal(ProtocolWriter writer) throws IOException {
        Marshaler.marshal(this, writer);
    }

    public void write(ProtocolWriter writer) throws IOException {
        writer.writeBegin();
        ProtocolWriter firstPassWriter = writer.getFirstPassWriter();
        if (firstPassWriter != null) {
            writeNested(firstPassWriter, false);
            writeNested(writer, false);
        } else {
            writeNested(writer, false);
        }
        writer.writeEnd();
    }

    public void writeNested(ProtocolWriter writer, boolean isBase) throws IOException {
        boolean hasCapability = writer.hasCapability(ProtocolCapability.CAN_OMIT_FIELDS);
        writer.writeStructBegin(Schema.metadata, isBase);
        super.writeNested(writer, true);
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 10, Schema.pageName_metadata);
        writer.writeWString(this.pageName);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 20, Schema.fromPage_metadata);
        writer.writeWString(this.fromPage);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        PageView that = (PageView) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    private boolean memberwiseCompareQuick(PageView that) {
        Object obj;
        boolean equals = super.memberwiseCompareQuick(that);
        if (equals) {
            if (this.pageName == null) {
                obj = 1;
            } else {
                obj = null;
            }
            if (obj == (that.pageName == null ? 1 : null)) {
                equals = this.pageName == null || this.pageName.length() == that.pageName.length();
                if (equals) {
                    if (this.fromPage != null) {
                        obj = 1;
                    } else {
                        obj = null;
                    }
                    if (obj == (that.fromPage != null ? 1 : null)) {
                        return this.fromPage == null || this.fromPage.length() == that.fromPage.length();
                    }
                }
            }
        }
        return false;
    }

    private boolean memberwiseCompareDeep(PageView that) {
        boolean equals;
        if (!super.memberwiseCompareDeep(that)) {
            equals = false;
        } else {
            equals = true;
        }
        return (equals && (this.pageName == null || this.pageName.equals(that.pageName))) && (this.fromPage == null || this.fromPage.equals(that.fromPage));
    }
}
