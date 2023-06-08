package tools.vitruv.change.serialization;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.lang.reflect.Field;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
/**
 *
 * Contains the logic of the serialization process, uses jackson to serialize EChange objects into Strings.
 * @author Edgar Hipp
 *
 */
public class EChangeSerializer extends StdSerializer<EChange> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	public EChangeSerializer() {
        super(EChange.class);
    }
	/**
	 * Defines the output for the ObjectMapper.writeValueAsString() function. Here a EChange object is deserialized to a String.
	 */
    @Override
    public void serialize(EChange change, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // Serialize the fields to JSON
    	
    	Class<? extends EChange> concreteImplementation = change.getClass();
    	
        Field[] fields = concreteImplementation.getDeclaredFields();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("classType", concreteImplementation.getSimpleName());
        
        for (Field field : fields) {
        	
        	field.setAccessible(true);
            // Get the name of the field
            String fieldName = field.getName();
            /*
            if (fieldName.equals("settingDelegate")) {
                continue;
            }
            */
            // Get the type of the field
            Class<?> fieldType = field.getType();
            
            // Get the value of the field
            Object fieldValue = null;
			try {
				fieldValue = field.get(change);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // Write the field name and value to the serialized data
			if (fieldValue!=null) {
				
	            jsonGenerator.writeFieldName(fieldName);
	            
	            jsonGenerator.writeObject(fieldValue);
			}
        }
        
        
    }
    /**
     * 
     * @param change
     * @return
     * @throws IOException
     */
    public String serializeFieldsToJson(EChange change) throws IOException {
    	
    	
    	Field[] declaredFields = CreateEObjectImpl.class.getDeclaredFields();
    	
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();

        // Get all fields of EChange using reflection
        Field[] fields = change.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if (fieldName.equals("settingDelegate")) {
                continue;
            }
            try {
                Object fieldValue = field.get(change);
                objectNode.putPOJO(fieldName, fieldValue);
            } catch (IllegalAccessException e) {
                // Handle exception if required
                e.printStackTrace();
            }
        }
        objectNode.put("classType", change.getClass().getName());
       
     
        return objectMapper.writeValueAsString(objectNode);
    }
}	
