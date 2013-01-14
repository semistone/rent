<<<<<<< HEAD
package org.siraya.rent.utils;

import org.junit.Test;
import java.util.List;
import junit.framework.Assert;
import org.yaml.snakeyaml.Yaml;

public class TestYaml {

	@Test 
    public void testLoad() {
        Yaml yaml = new Yaml();
        String document = "\n- Hesperiidae\n- Papilionidae\n- Apatelodidae\n- Epiplemidae";
        List<String> list = (List<String>) yaml.load(document);
        Assert.assertEquals("[Hesperiidae, Papilionidae, Apatelodidae, Epiplemidae]", list.toString());
    }

}
=======
package org.siraya.rent.utils;

import org.junit.Test;
import java.util.List;
import junit.framework.Assert;
import org.yaml.snakeyaml.Yaml;

public class TestYaml {

	@Test 
    public void testLoad() {
        Yaml yaml = new Yaml();
        String document = "\n- Hesperiidae\n- Papilionidae\n- Apatelodidae\n- Epiplemidae";
        List<String> list = (List<String>) yaml.load(document);
        Assert.assertEquals("[Hesperiidae, Papilionidae, Apatelodidae, Epiplemidae]", list.toString());
    }

}
>>>>>>> master
