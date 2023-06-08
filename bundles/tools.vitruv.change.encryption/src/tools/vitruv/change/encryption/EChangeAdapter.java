package tools.vitruv.change.encryption;

import java.util.Optional;

import tools.vitruv.change.atomic.EChange;

public interface EChangeAdapter {
	
	Optional<String> serialize(EChange eChange);
    
	Optional<EChange> deserialize(String serializedData);
}
