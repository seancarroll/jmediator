package jmediator.dropwizard;

import org.junit.jupiter.api.Test;

class ScanningJmediatorBundleTest {

    @Test
    public void test() {
        String packages = "jmediator.dropwizard.fake.entities.pckg";

        ScanningJmediatorBundle.findEntityClassesFromDirectory(new String[]{packages});
    }



    @Test
    public void testFindEntityClassesFromDirectory() {
//        //given
//        String packageWithEntities = "io.dropwizard.hibernate.fake.entities.pckg";
//        //when
//        List<Class<?>> findEntityClassesFromDirectory =
//            ScanningHibernateBundle.findEntityClassesFromDirectory(new String[]{packageWithEntities});
//
//        //then
//        assertFalse(findEntityClassesFromDirectory.isEmpty());
//        assertEquals(4, findEntityClassesFromDirectory.size());
    }

    @Test
    public void testFindEntityClassesFromMultipleDirectories() {
//        //given
//        String packageWithEntities = "io.dropwizard.hibernate.fake.entities.pckg";
//        String packageWithEntities2 = "io.dropwizard.hibernate.fake2.entities.pckg";
//        //when
//        List<Class<?>> findEntityClassesFromDirectory =
//            ScanningHibernateBundle.findEntityClassesFromDirectory(new String[]{packageWithEntities, packageWithEntities2});
//
//        //then
//        assertFalse(findEntityClassesFromDirectory.isEmpty());
//        assertEquals(8, findEntityClassesFromDirectory.size());
    }
}
