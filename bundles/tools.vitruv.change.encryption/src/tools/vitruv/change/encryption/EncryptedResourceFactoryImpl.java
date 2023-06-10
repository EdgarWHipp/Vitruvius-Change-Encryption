package tools.vitruv.change.encryption;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import tools.vitruv.change.encryption.impl.EncryptedResourceImpl;

public class EncryptedResourceFactoryImpl extends XMIResourceFactoryImpl{
	public EncryptedResourceFactoryImpl() {
		super();
		}
	public Resource createResource(URI uri) {
		XMLResource result = new EncryptedResourceImpl(uri);
		result.setEncoding("utf-8");
		return result;
		}
}
