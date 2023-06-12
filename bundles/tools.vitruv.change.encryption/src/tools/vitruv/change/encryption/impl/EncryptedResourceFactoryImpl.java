package tools.vitruv.change.encryption.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class EncryptedResourceFactoryImpl extends XMIResourceFactoryImpl{
	public EncryptedResourceFactoryImpl() {
		super();
		}
	public Resource createResource(URI uri) {
		XMLResource result = new EncryptedResourceImpl(uri);
		result.setEncoding("UTF-8");
		return result;
		}
}
