package tools.vitruv.change.encryption.impl.differentkeys;

import javax.crypto.SecretKey;

import tools.vitruv.change.atomic.EChange;

public class ChangeAndKey {
	private final EChange change;
	private final SecretKey key;
	public ChangeAndKey(EChange change, SecretKey key) {
		this.change = change;
		this.key = key;
	}
	public SecretKey getKey() {
		return this.key;
	}
	public EChange getChange() {
		return this.change;
	}
}
