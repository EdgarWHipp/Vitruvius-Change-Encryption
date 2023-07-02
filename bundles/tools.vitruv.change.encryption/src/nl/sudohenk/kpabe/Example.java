package nl.sudohenk.kpabe;

import java.io.File;

import nl.sudohenk.kpabe.gpswabe.gpswabePolicy;

public class Example {

    
    public static void main(String[] args) throws Exception {
        
    	String test_folder = new File("").getAbsolutePath() +"/example/";
        String curveparamsFileLocation = test_folder + "curveparams";
        
        KeyPolicyAttributeBasedEncryption kpabe = new KeyPolicyAttributeBasedEncryption();
        String pubfile = test_folder + "publickey";
        String mskfile = test_folder + "mastersecretkey";
        String[] attrs_univ = {"application1", "module1", "solution1"};
        kpabe.setup(pubfile, mskfile, attrs_univ, curveparamsFileLocation);
        String prvfile = test_folder + "policy";
        // Build up an access tree:
        // Example of what we want to achieve:
        //                          2 of 2
        //                         /      \
        //                  solution1    1 of 2
        //                              /       \
        //                      application1   module1
        //
        // This access tree can also be written as:
        //      solution1 1of1 application1 module1 1of2 2of2
        // Which can be simplified to:
        //      (solution1 AND (application1 OR module1))ß
        
        // "solution1" (leaf)
        gpswabePolicy sub1_policy = new gpswabePolicy("solution1", 1, null);
        // "application1 or module1" (1 out of 2)
        gpswabePolicy sub2_policy = new gpswabePolicy(null, 1, null);
        gpswabePolicy[] sub2_children = new gpswabePolicy[] {new gpswabePolicy("application1", 1, null), new gpswabePolicy("module1", 1, null)};
        sub2_policy.setChildren(sub2_children);
        
        // assemble policy tree into the root
        gpswabePolicy policy = new gpswabePolicy(null, 2, null);
        gpswabePolicy[] policy_children = new gpswabePolicy[] {sub1_policy, sub2_policy};
        policy.setChildren(policy_children);
        
        kpabe.keygen(pubfile, mskfile, prvfile, policy);
        
        try {
            byte[] plaintext = "Hello!".getBytes();
            byte[] ciphertext = kpabe.enc(pubfile, plaintext, attrs_univ);
            byte[] result = kpabe.dec(pubfile, prvfile, ciphertext);
        } catch(Exception e) {
        }
    }
}
