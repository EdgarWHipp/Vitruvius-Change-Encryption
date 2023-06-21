package tools.vitruv.change.encryption.impl;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.EChangeAdapter;
import tools.vitruv.change.serialization.EChangeDeserializer;
import tools.vitruv.change.serialization.EChangeSerializer;
/**
 * Handles the creation of the Objectmapper for serialization and deserialization.
 * Returns an appropriate Optional<String> in the serialize function, and an Optional<EChange> in the deserialize function.
 * @author Edgar Hipp
 *
 */
public class EChangeAdapterImpl implements EChangeAdapter {
	
	/*
	 * Returns the Objectmapper with both the EChangeSerializaer() and the EChangeDeserializer() properties.
	 */
	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(EChange.class, new EChangeSerializer());
        module.addDeserializer(EChange.class, new EChangeDeserializer());
        
        objectMapper.registerModule(module);
        return objectMapper;
	}
	/**
	 * Returns the serialized String that was created from the passed EChange, 
	 * if the EChange is not defined correctly an empty Optional will be returned.
	 */
	@Override
	public Optional<String> serialize(EChange eChange) {
	    try {
	       // should serialize the EChange proxy !
	        return Optional.of(getObjectMapper().writeValueAsString(eChange));
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return Optional.empty();
	    }
	}
	/**
	 * Returns the EChange object that was created out of the serialized String, 
	 * if the serialized data does not contain information to create a valid EChange object an empty Optional is returned.
	 */
	@Override
	public Optional<EChange> deserialize(String serializedData) {
	    try {
	    	//should deserialize a EChangeProxy
	        return Optional.of(getObjectMapper().readValue(serializedData, EChange.class));
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return Optional.empty();
	    }
	}

}
