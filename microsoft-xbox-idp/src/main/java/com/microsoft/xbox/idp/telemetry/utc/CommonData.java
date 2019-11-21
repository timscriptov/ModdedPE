package com.microsoft.xbox.idp.telemetry.utc;

import android.support.v4.media.TransportMediator;

import com.facebook.GraphRequest;
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

import Microsoft.Telemetry.Base;

public class CommonData extends Base {
    private String accessibilityInfo;
    private String additionalInfo;
    private String appName;
    private String appSessionId;
    private String clientLanguage;
    private String deviceModel;
    private String eventVersion;
    private int network;
    private String sandboxId;
    private String titleDeviceId;
    private String titleSessionId;
    private String userId;
    private String xsapiVersion;

    public static class Schema {
        public static final Metadata accessibilityInfo_metadata = new Metadata();
        public static final Metadata additionalInfo_metadata = new Metadata();
        public static final Metadata appName_metadata = new Metadata();
        public static final Metadata appSessionId_metadata = new Metadata();
        public static final Metadata clientLanguage_metadata = new Metadata();
        public static final Metadata deviceModel_metadata = new Metadata();
        public static final Metadata eventVersion_metadata = new Metadata();
        public static final Metadata metadata = new Metadata();
        public static final Metadata network_metadata = new Metadata();
        public static final Metadata sandboxId_metadata = new Metadata();
        public static final SchemaDef schemaDef = new SchemaDef();
        public static final Metadata titleDeviceId_metadata = new Metadata();
        public static final Metadata titleSessionId_metadata = new Metadata();
        public static final Metadata userId_metadata = new Metadata();
        public static final Metadata xsapiVersion_metadata = new Metadata();

        static {
            metadata.setName("CommonData");
            metadata.setQualified_name("com.microsoft.xbox.idp.telemetry.utc.CommonData");
            metadata.getAttributes().put("Description", "OnlineId base event with required fields");
            eventVersion_metadata.setName("eventVersion");
            eventVersion_metadata.setModifier(Modifier.Required);
            eventVersion_metadata.getAttributes().put("Description", "The event's version in the form of A.B.C where each subfield is the version for Part A, B, or C respectively.  This helps the backend cookers and processers adjust to different versions of the event schema");
            deviceModel_metadata.setName("deviceModel");
            deviceModel_metadata.setModifier(Modifier.Required);
            deviceModel_metadata.getAttributes().put("Description", "The specific model of the device.  On Android this is from the constant: android.os.Build.MODEL.  NOTE: For completeness, one should prepend android.os.Build.MANUFACTURER to this value if the MFG name is not part of the model name.");
            xsapiVersion_metadata.setName("xsapiVersion");
            xsapiVersion_metadata.setModifier(Modifier.Required);
            xsapiVersion_metadata.getAttributes().put("Description", "The xsapi version.  Should get this from the xsapi build properties");
            appName_metadata.setName("appName");
            appName_metadata.setModifier(Modifier.Required);
            appName_metadata.getAttributes().put("Description", "The application name");
            clientLanguage_metadata.setName("clientLanguage");
            clientLanguage_metadata.setModifier(Modifier.Required);
            clientLanguage_metadata.getAttributes().put("Description", "The system language-region (for example, en-US = english in USA).");
            network_metadata.setName("network");
            network_metadata.setModifier(Modifier.Required);
            network_metadata.getAttributes().put("Description", "The network connection being used (0 = unknown | 1 = wifi | 2 = cellular | 3 = wired)");
            network_metadata.getDefault_value().setUint_value(0);
            sandboxId_metadata.setName("sandboxId");
            sandboxId_metadata.setModifier(Modifier.Required);
            sandboxId_metadata.getAttributes().put("Description", "The xsapi sandbox for service calls");
            appSessionId_metadata.setName("appSessionId");
            appSessionId_metadata.setModifier(Modifier.Required);
            appSessionId_metadata.getAttributes().put("Description", "The sessionId for the app; gets set on first use of telemetry -- useful for binding events together into scenarios and analyzing flow");
            userId_metadata.setName("userId");
            userId_metadata.setModifier(Modifier.Required);
            userId_metadata.getAttributes().put("Description", "The User Id");
            additionalInfo_metadata.setName("additionalInfo");
            additionalInfo_metadata.setModifier(Modifier.Required);
            additionalInfo_metadata.getAttributes().put("Description", "The json key-value collection of data that gives greater meaning to the event");
            accessibilityInfo_metadata.setName("accessibilityInfo");
            accessibilityInfo_metadata.setModifier(Modifier.Required);
            accessibilityInfo_metadata.getAttributes().put("Description", "The json key-value collection of accessibility settings -- information within will differ by platform");
            titleDeviceId_metadata.setName("titleDeviceId");
            titleDeviceId_metadata.setModifier(Modifier.Required);
            titleDeviceId_metadata.getAttributes().put("Description", "The device guid from the title hosting xsapi idp or tcui");
            titleSessionId_metadata.setName("titleSessionId");
            titleSessionId_metadata.setModifier(Modifier.Required);
            titleSessionId_metadata.getAttributes().put("Description", "The session guid from the title hosting xsapi idp or tcui");
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
                    structDef.setBase_def(Microsoft.Telemetry.Base.Schema.getTypeDef(schema));
                    FieldDef field = new FieldDef();
                    field.setId((short) 10);
                    field.setMetadata(eventVersion_metadata);
                    field.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field);
                    FieldDef field2 = new FieldDef();
                    field2.setId((short) 20);
                    field2.setMetadata(deviceModel_metadata);
                    field2.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field2);
                    FieldDef field3 = new FieldDef();
                    field3.setId((short) 30);
                    field3.setMetadata(xsapiVersion_metadata);
                    field3.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field3);
                    FieldDef field4 = new FieldDef();
                    field4.setId((short) 40);
                    field4.setMetadata(appName_metadata);
                    field4.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field4);
                    FieldDef field5 = new FieldDef();
                    field5.setId((short) 50);
                    field5.setMetadata(clientLanguage_metadata);
                    field5.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field5);
                    FieldDef field6 = new FieldDef();
                    field6.setId((short) 60);
                    field6.setMetadata(network_metadata);
                    field6.getType().setId(BondDataType.BT_UINT32);
                    structDef.getFields().add(field6);
                    FieldDef field7 = new FieldDef();
                    field7.setId((short) 70);
                    field7.setMetadata(sandboxId_metadata);
                    field7.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field7);
                    FieldDef field8 = new FieldDef();
                    field8.setId((short) 80);
                    field8.setMetadata(appSessionId_metadata);
                    field8.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field8);
                    FieldDef field9 = new FieldDef();
                    field9.setId((short) 90);
                    field9.setMetadata(userId_metadata);
                    field9.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field9);
                    FieldDef field10 = new FieldDef();
                    field10.setId((short) 100);
                    field10.setMetadata(additionalInfo_metadata);
                    field10.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field10);
                    FieldDef field11 = new FieldDef();
                    field11.setId((short) 110);
                    field11.setMetadata(accessibilityInfo_metadata);
                    field11.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field11);
                    FieldDef field12 = new FieldDef();
                    field12.setId((short) 120);
                    field12.setMetadata(titleDeviceId_metadata);
                    field12.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field12);
                    FieldDef field13 = new FieldDef();
                    field13.setId((short) 130);
                    field13.setMetadata(titleSessionId_metadata);
                    field13.getType().setId(BondDataType.BT_WSTRING);
                    structDef.getFields().add(field13);
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

    public final String getEventVersion() {
        return this.eventVersion;
    }

    public final void setEventVersion(String value) {
        this.eventVersion = value;
    }

    public final String getDeviceModel() {
        return this.deviceModel;
    }

    public final void setDeviceModel(String value) {
        this.deviceModel = value;
    }

    public final String getXsapiVersion() {
        return this.xsapiVersion;
    }

    public final void setXsapiVersion(String value) {
        this.xsapiVersion = value;
    }

    public final String getAppName() {
        return this.appName;
    }

    public final void setAppName(String value) {
        this.appName = value;
    }

    public final String getClientLanguage() {
        return this.clientLanguage;
    }

    public final void setClientLanguage(String value) {
        this.clientLanguage = value;
    }

    public final int getNetwork() {
        return this.network;
    }

    public final void setNetwork(int value) {
        this.network = value;
    }

    public final String getSandboxId() {
        return this.sandboxId;
    }

    public final void setSandboxId(String value) {
        this.sandboxId = value;
    }

    public final String getAppSessionId() {
        return this.appSessionId;
    }

    public final void setAppSessionId(String value) {
        this.appSessionId = value;
    }

    public final String getUserId() {
        return this.userId;
    }

    public final void setUserId(String value) {
        this.userId = value;
    }

    public final String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public final void setAdditionalInfo(String value) {
        this.additionalInfo = value;
    }

    public final String getAccessibilityInfo() {
        return this.accessibilityInfo;
    }

    public final void setAccessibilityInfo(String value) {
        this.accessibilityInfo = value;
    }

    public final String getTitleDeviceId() {
        return this.titleDeviceId;
    }

    public final void setTitleDeviceId(String value) {
        this.titleDeviceId = value;
    }

    public final String getTitleSessionId() {
        return this.titleSessionId;
    }

    public final void setTitleSessionId(String value) {
        this.titleSessionId = value;
    }

    public Object getField(FieldDef fieldDef) {
        switch (fieldDef.getId()) {
            case 10:
                return this.eventVersion;
            case 20:
                return this.deviceModel;
            case 30:
                return this.xsapiVersion;
            case 40:
                return this.appName;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                return this.clientLanguage;
            case 60:
                return this.network;
            case 70:
                return this.sandboxId;
            case 80:
                return this.appSessionId;
            case 90:
                return this.userId;
            case 100:
                return this.additionalInfo;
            case 110:
                return this.accessibilityInfo;
            case 120:
                return this.titleDeviceId;
            case TransportMediator.KEYCODE_MEDIA_RECORD /*130*/:
                return this.titleSessionId;
            default:
                return null;
        }
    }

    public void setField(FieldDef fieldDef, Object value) {
        switch (fieldDef.getId()) {
            case 10:
                this.eventVersion = (String) value;
                return;
            case 20:
                this.deviceModel = (String) value;
                return;
            case 30:
                this.xsapiVersion = (String) value;
                return;
            case 40:
                this.appName = (String) value;
                return;
            case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                this.clientLanguage = (String) value;
                return;
            case 60:
                this.network = (Integer) value;
                return;
            case 70:
                this.sandboxId = (String) value;
                return;
            case 80:
                this.appSessionId = (String) value;
                return;
            case 90:
                this.userId = (String) value;
                return;
            case 100:
                this.additionalInfo = (String) value;
                return;
            case 110:
                this.accessibilityInfo = (String) value;
                return;
            case 120:
                this.titleDeviceId = (String) value;
                return;
            case TransportMediator.KEYCODE_MEDIA_RECORD /*130*/:
                this.titleSessionId = (String) value;
                return;
            default:
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
        reset("CommonData", "com.microsoft.xbox.idp.telemetry.utc.CommonData");
    }

    public void reset(String name, String qualifiedName) {
        super.reset(name, qualifiedName);
        this.eventVersion = "";
        this.deviceModel = "";
        this.xsapiVersion = "";
        this.appName = "";
        this.clientLanguage = "";
        this.network = 0;
        this.sandboxId = "";
        this.appSessionId = "";
        this.userId = "";
        this.additionalInfo = "";
        this.accessibilityInfo = "";
        this.titleDeviceId = "";
        this.titleSessionId = "";
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
            this.eventVersion = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.deviceModel = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.xsapiVersion = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.appName = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.clientLanguage = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.network = reader.readUInt32();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.sandboxId = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.appSessionId = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.userId = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.additionalInfo = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.accessibilityInfo = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.titleDeviceId = reader.readWString();
        }
        if (!canOmitFields || !reader.readFieldOmitted()) {
            this.titleSessionId = reader.readWString();
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
                        this.eventVersion = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 20:
                        this.deviceModel = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 30:
                        this.xsapiVersion = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 40:
                        this.appName = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case GraphRequest.MAXIMUM_BATCH_SIZE /*50*/:
                        this.clientLanguage = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 60:
                        this.network = ReadHelper.readUInt32(reader, fieldTag.type);
                        break;
                    case 70:
                        this.sandboxId = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 80:
                        this.appSessionId = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 90:
                        this.userId = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 100:
                        this.additionalInfo = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 110:
                        this.accessibilityInfo = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case 120:
                        this.titleDeviceId = ReadHelper.readWString(reader, fieldTag.type);
                        break;
                    case TransportMediator.KEYCODE_MEDIA_RECORD /*130*/:
                        this.titleSessionId = ReadHelper.readWString(reader, fieldTag.type);
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
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 10, Schema.eventVersion_metadata);
        writer.writeWString(this.eventVersion);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 20, Schema.deviceModel_metadata);
        writer.writeWString(this.deviceModel);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 30, Schema.xsapiVersion_metadata);
        writer.writeWString(this.xsapiVersion);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 40, Schema.appName_metadata);
        writer.writeWString(this.appName);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 50, Schema.clientLanguage_metadata);
        writer.writeWString(this.clientLanguage);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_UINT32, 60, Schema.network_metadata);
        writer.writeUInt32(this.network);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 70, Schema.sandboxId_metadata);
        writer.writeWString(this.sandboxId);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 80, Schema.appSessionId_metadata);
        writer.writeWString(this.appSessionId);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 90, Schema.userId_metadata);
        writer.writeWString(this.userId);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 100, Schema.additionalInfo_metadata);
        writer.writeWString(this.additionalInfo);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 110, Schema.accessibilityInfo_metadata);
        writer.writeWString(this.accessibilityInfo);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, 120, Schema.titleDeviceId_metadata);
        writer.writeWString(this.titleDeviceId);
        writer.writeFieldEnd();
        writer.writeFieldBegin(BondDataType.BT_WSTRING, TransportMediator.KEYCODE_MEDIA_RECORD, Schema.titleSessionId_metadata);
        writer.writeWString(this.titleSessionId);
        writer.writeFieldEnd();
        writer.writeStructEnd(isBase);
    }

    public boolean memberwiseCompare(Object obj) {
        if (obj == null) {
            return false;
        }
        CommonData that = (CommonData) obj;
        if (!memberwiseCompareQuick(that) || !memberwiseCompareDeep(that)) {
            return false;
        }
        return true;
    }

    private boolean memberwiseCompareQuick(CommonData that) {
        Object obj;
        boolean equals = super.memberwiseCompareQuick(that);
        if (equals) {
            if (this.eventVersion == null) {
                obj = 1;
            } else {
                obj = null;
            }
            if (obj == (that.eventVersion == null ? 1 : null)) {
                equals = this.eventVersion == null || this.eventVersion.length() == that.eventVersion.length();
                if (equals) {
                    if (this.deviceModel != null) {
                        obj = 1;
                    } else {
                        obj = null;
                    }
                    if (obj == (that.deviceModel != null ? 1 : null)) {
                        equals = this.deviceModel == null || this.deviceModel.length() == that.deviceModel.length();
                        if (equals) {
                            if (this.xsapiVersion != null) {
                                obj = 1;
                            } else {
                                obj = null;
                            }
                            if (obj == (that.xsapiVersion != null ? 1 : null)) {
                                equals = this.xsapiVersion == null || this.xsapiVersion.length() == that.xsapiVersion.length();
                                if (equals) {
                                    if (this.appName != null) {
                                        obj = 1;
                                    } else {
                                        obj = null;
                                    }
                                    if (obj == (that.appName != null ? 1 : null)) {
                                        equals = this.appName == null || this.appName.length() == that.appName.length();
                                        if (equals) {
                                            if (this.clientLanguage != null) {
                                                obj = 1;
                                            } else {
                                                obj = null;
                                            }
                                            if (obj == (that.clientLanguage != null ? 1 : null)) {
                                                equals = this.clientLanguage == null || this.clientLanguage.length() == that.clientLanguage.length();
                                                equals = !equals && this.network == that.network;
                                                if (equals) {
                                                    if (this.sandboxId != null) {
                                                        obj = 1;
                                                    } else {
                                                        obj = null;
                                                    }
                                                    if (obj == (that.sandboxId != null ? 1 : null)) {
                                                        equals = this.sandboxId == null || this.sandboxId.length() == that.sandboxId.length();
                                                        if (equals) {
                                                            if (this.appSessionId != null) {
                                                                obj = 1;
                                                            } else {
                                                                obj = null;
                                                            }
                                                            if (obj == (that.appSessionId != null ? 1 : null)) {
                                                                equals = this.appSessionId == null || this.appSessionId.length() == that.appSessionId.length();
                                                                if (equals) {
                                                                    if (this.userId != null) {
                                                                        obj = 1;
                                                                    } else {
                                                                        obj = null;
                                                                    }
                                                                    if (obj == (that.userId != null ? 1 : null)) {
                                                                        equals = this.userId == null || this.userId.length() == that.userId.length();
                                                                        if (equals) {
                                                                            if (this.additionalInfo != null) {
                                                                                obj = 1;
                                                                            } else {
                                                                                obj = null;
                                                                            }
                                                                            if (obj == (that.additionalInfo != null ? 1 : null)) {
                                                                                equals = this.additionalInfo == null || this.additionalInfo.length() == that.additionalInfo.length();
                                                                                if (equals) {
                                                                                    if (this.accessibilityInfo != null) {
                                                                                        obj = 1;
                                                                                    } else {
                                                                                        obj = null;
                                                                                    }
                                                                                    if (obj == (that.accessibilityInfo != null ? 1 : null)) {
                                                                                        equals = this.accessibilityInfo == null || this.accessibilityInfo.length() == that.accessibilityInfo.length();
                                                                                        if (equals) {
                                                                                            if (this.titleDeviceId != null) {
                                                                                                obj = 1;
                                                                                            } else {
                                                                                                obj = null;
                                                                                            }
                                                                                            if (obj == (that.titleDeviceId != null ? 1 : null)) {
                                                                                                equals = this.titleDeviceId == null || this.titleDeviceId.length() == that.titleDeviceId.length();
                                                                                                if (equals) {
                                                                                                    if (this.titleSessionId != null) {
                                                                                                        obj = 1;
                                                                                                    } else {
                                                                                                        obj = null;
                                                                                                    }
                                                                                                    if (obj == (that.titleSessionId != null ? 1 : null)) {
                                                                                                        return this.titleSessionId == null || this.titleSessionId.length() == that.titleSessionId.length();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean memberwiseCompareDeep(CommonData that) {
        return ((((((((((((super.memberwiseCompareDeep(that)) && (this.eventVersion == null || this.eventVersion.equals(that.eventVersion))) && (this.deviceModel == null || this.deviceModel.equals(that.deviceModel))) && (this.xsapiVersion == null || this.xsapiVersion.equals(that.xsapiVersion))) && (this.appName == null || this.appName.equals(that.appName))) && (this.clientLanguage == null || this.clientLanguage.equals(that.clientLanguage))) && (this.sandboxId == null || this.sandboxId.equals(that.sandboxId))) && (this.appSessionId == null || this.appSessionId.equals(that.appSessionId))) && (this.userId == null || this.userId.equals(that.userId))) && (this.additionalInfo == null || this.additionalInfo.equals(that.additionalInfo))) && (this.accessibilityInfo == null || this.accessibilityInfo.equals(that.accessibilityInfo))) && (this.titleDeviceId == null || this.titleDeviceId.equals(that.titleDeviceId))) && (this.titleSessionId == null || this.titleSessionId.equals(that.titleSessionId));
    }
}
