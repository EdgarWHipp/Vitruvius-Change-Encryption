package tools.vitruv.change.serialization;

import java.io.IOException;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl;
import tools.vitruv.change.atomic.root.impl.InsertRootEObjectImpl;
/**
 * Contains the logic of the deserialization process, uses jackson to deserialize Strings to the correct concrete EChange subtype.
 * @author Edgar Hipp
 *
 */
public class EChangeDeserializer extends StdDeserializer<EChange> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EChangeDeserializer() {
        super(EChange.class);
    }
	
	@Override
    public EChange deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readValue(p, JsonNode.class);
	    JsonNode classTypeNode = rootNode.get("classType");
	    
	    
	    
	
	    switch (classTypeNode.asText()) {

	    case "CreateEObjectImpl":
	    	 
	      // Handle deserialization for CreateEObjectImpl
	      // Example:
	      CreateEObjectImpl<Member> createEObjectImpl = new CreateEObjectImpl<Member>();
	      Member member = FamiliesFactory.eINSTANCE.createMember();
		  member.setFirstName("Clara");
	      createEObjectImpl.setAffectedEObjectID("123");
	      createEObjectImpl.setIdAttributeValue("test");
	      createEObjectImpl.setAffectedEObjectType(FamiliesFactory.eINSTANCE.getFamiliesPackage().getMember());
	      createEObjectImpl.setAffectedEObject(member);
	      // Set specific properties for CreateEObjectImpl
	      // ...

	      return createEObjectImpl;

	    case "ReplaceSingleValuedEAttributeImpl":
	      // Handle deserialization for CreateERootObjectImpl
	      // Example:
	      //CreateERootObjectImpl createERootObjectImpl = new CreateERootObjectImpl();
	      // Set specific properties for CreateERootObjectImpl
	      // ...
	      
	      return new ReplaceSingleValuedEAttributeImpl();
	    case "InsertRootEObjectImpl":
	    	 return new InsertRootEObjectImpl();
	    default:
	      // Handle unknown classType or default case
	      
	      
	      return null;
	    }
	    
	}

}
