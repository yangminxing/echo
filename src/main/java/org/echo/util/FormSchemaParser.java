package org.echo.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FormSchemaParser {

    private static final Map<String, String> COMPONENT_TYPE_MAP = new HashMap<>();

    static {
        COMPONENT_TYPE_MAP.put("input", "VARCHAR(255)");
        COMPONENT_TYPE_MAP.put("textarea", "TEXT");
        COMPONENT_TYPE_MAP.put("number", "DECIMAL(20,4)");
        COMPONENT_TYPE_MAP.put("select", "VARCHAR(100)");
        COMPONENT_TYPE_MAP.put("radio", "VARCHAR(100)");
        COMPONENT_TYPE_MAP.put("checkbox", "VARCHAR(500)");
        COMPONENT_TYPE_MAP.put("date", "DATE");
        COMPONENT_TYPE_MAP.put("date-range", "VARCHAR(100)");
        COMPONENT_TYPE_MAP.put("time", "TIME");
        COMPONENT_TYPE_MAP.put("switch", "TINYINT(1)");
        COMPONENT_TYPE_MAP.put("rate", "INT");
        COMPONENT_TYPE_MAP.put("slider", "INT");
        COMPONENT_TYPE_MAP.put("upload", "VARCHAR(500)");
        COMPONENT_TYPE_MAP.put("rich-text", "LONGTEXT");
        COMPONENT_TYPE_MAP.put("password", "VARCHAR(255)");
        COMPONENT_TYPE_MAP.put("color", "VARCHAR(20)");
        COMPONENT_TYPE_MAP.put("cascader", "VARCHAR(500)");
    }

    public List<FieldInfo> parseSchema(String formSchema) {
        List<FieldInfo> fields = new ArrayList<>();
        if (formSchema == null || formSchema.isEmpty()) {
            return fields;
        }

        try {
            JSONObject schema = JSONObject.parseObject(formSchema);
            JSONArray schemas = schema.getJSONArray("schemas");
            if (schemas == null || schemas.isEmpty()) {
                return fields;
            }

            for (int i = 0; i < schemas.size(); i++) {
                JSONObject pageSchema = schemas.getJSONObject(i);
                JSONArray children = pageSchema.getJSONArray("children");
                if (children != null) {
                    parseChildren(children, fields);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("解析表单Schema失败: " + e.getMessage(), e);
        }

        return fields;
    }

    private void parseChildren(JSONArray children, List<FieldInfo> fields) {
        for (int i = 0; i < children.size(); i++) {
            JSONObject component = children.getJSONObject(i);
            String type = component.getString("type");

            if ("row".equals(type) || "col".equals(type) || "card".equals(type) || "tabs".equals(type)) {
                JSONArray nestedChildren = component.getJSONArray("children");
                if (nestedChildren != null) {
                    parseChildren(nestedChildren, fields);
                }
            } else if (COMPONENT_TYPE_MAP.containsKey(type)) {
                FieldInfo fieldInfo = extractFieldInfo(component);
                if (fieldInfo != null && fieldInfo.getField() != null && !fieldInfo.getField().isEmpty()) {
                    fields.add(fieldInfo);
                }
            }
        }
    }

    private FieldInfo extractFieldInfo(JSONObject component) {
        String type = component.getString("type");
        String field = component.getString("field");
        String label = component.getString("label");

        if (field == null || field.isEmpty()) {
            return null;
        }

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setType(type);
        fieldInfo.setField(field);
        fieldInfo.setLabel(label != null ? label : field);
        fieldInfo.setSqlType(COMPONENT_TYPE_MAP.getOrDefault(type, "VARCHAR(255)"));

        return fieldInfo;
    }

    public String generateCreateTableSQL(String tableName, String formSchema, String tableComment) {
        List<FieldInfo> fields = parseSchema(formSchema);
        if (fields.isEmpty()) {
            throw new RuntimeException("表单Schema中没有可用的字段");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE `").append(tableName).append("` (\n");
        sql.append("  `id` VARCHAR(36) PRIMARY KEY COMMENT '主键',\n");

        for (FieldInfo field : fields) {
            sql.append("  `").append(field.getField()).append("` ")
               .append(field.getSqlType())
               .append(" COMMENT '").append(field.getLabel()).append("',\n");
        }

        sql.append("  `create_time` DATETIME COMMENT '创建时间',\n");
        sql.append("  `update_time` DATETIME COMMENT '更新时间'\n");
        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
        
        if (tableComment != null && !tableComment.isEmpty()) {
            sql.append(" COMMENT='").append(tableComment).append("'");
        }
        sql.append(";");

        return sql.toString();
    }

    public static class FieldInfo {
        private String type;
        private String field;
        private String label;
        private String sqlType;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getSqlType() {
            return sqlType;
        }

        public void setSqlType(String sqlType) {
            this.sqlType = sqlType;
        }
    }
}
