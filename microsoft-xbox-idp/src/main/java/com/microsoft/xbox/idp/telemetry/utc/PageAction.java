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

public class PageAction extends Data<CommonData> {
    private String actionName;
    private String pageName;

    public static class Schema {
        public static final Metadata actionName_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final Metadata pageName_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();

        static {
            metadata.setName("PageAction");
            metadata.setQualified_name("com.microsoft.xbox.idp.telemetry.utc.PageAction");
            metadata.getAttributes().put("Description", "OnlineId PageAction event");
            actionName_metadata.setName("actionName");
            actionName_metadata.setModifier(Modifier.Required);
            actionName_metadata.getAttributes().put("Description", "The name of the action taking place");
            pageName_metadata.setName("pageName");
            pageName_metadata.setModifier(Modifier.Required);
            pageName_metadata.getAttributes().put("Description", "The name of the page the action is taking place upon");
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
                    field.setMetadata(actionName_metadata);
                    field.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId((short) 20);
                    field2.setMetadata(pageName_metadata);
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

    public final String getActionName() {
        return this.actionName;
    }

    public final void setActionName(String value) {
        this.actionName = value;
    }

    public final String getPageName() {
        return this.pageName;
    }

    public final void setPageName(String value) {
        this.pageName = value;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case 10:
                return this.actionName;
            case 20:
                return this.pageName;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.actionName = (String) value;
                return;
            case 20:
                this.pageName = (String) value;
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
        reset("PageAction", "com.microsoft.xbox.idp.telemetry.utc.PageAction");
    }

    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.actionName = "";
        this.pageName = "";
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
            this.actionName = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.pageName = reader.readWString();
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
                        this.actionName = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.pageName = ReadHelper.readWString(reader, fieldTag.type);
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
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 10, Schema.actionName_metadata);
        writer.writeWString(this.actionName);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 20, Schema.pageName_metadata);
        writer.writeWString(this.pageName);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        PageAction that = (PageAction) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    private boolean memberwiseCompareQuick(PageAction that) {
        Object obj;
        boolean equals = super.memberwiseCompareQuick(that);
        if (equals) {
            if (this.actionName == null) {
                obj = 1;
            } else {
                obj = null;
            }
            if (obj == (that.actionName == null ? 1 : null)) {
                equals = this.actionName == null || this.actionName.length() == that.actionName.length();
                if (equals) {
                    if (this.pageName != null) {
                        obj = 1;
                    } else {
                        obj = null;
                    }
                    if (obj == (that.pageName != null ? 1 : null)) {
                        return this.pageName == null || this.pageName.length() == that.pageName.length();
                    }
                }
            }
        }
        return false;
    }

    private boolean memberwiseCompareDeep(PageAction that) {
        boolean equals;
        if (!super.memberwiseCompareDeep(that)) {
            equals = false;
        } else {
            equals = true;
        }
        return (equals && (this.actionName == null || this.actionName.equals(that.actionName))) && (this.pageName == null || this.pageName.equals(that.pageName));
    }
}
