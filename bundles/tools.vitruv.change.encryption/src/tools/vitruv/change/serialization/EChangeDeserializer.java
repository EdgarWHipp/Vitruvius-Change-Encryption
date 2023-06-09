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
import tools.vitruv.change.atomic.AtomicFactory;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.TypeInferringCompoundEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
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
	    	

	      Member member = FamiliesFactory.eINSTANCE.createMember();
	      CreateEObject<Member> createEObjectImpl = TypeInferringAtomicEChangeFactory.getInstance().createCreateEObjectChange(member);
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
	    	TypeInferringAtomicEChangeFactory.getInstance().createReplaceSingleAttributeChange(null, null, null, null);
	    case "InsertRootEObjectImpl":
	    	TypeInferringCompoundEChangeFactory.getInstance().createCreateAndInsertRootChange(null, null, 0);
	    default:
	      // Handle unknown classType or default case
	      
	      
	      return null;
	    }
	    
	}

}
