// Copyright 2008 Google Inc. All Rights Reserved.
package org.datanucleus.store.appengine;

import com.google.apphosting.api.datastore.Entity;
import com.google.apphosting.api.datastore.EntityNotFoundException;
import com.google.apphosting.api.datastore.KeyFactory;

import org.datanucleus.test.Flight;
import org.datanucleus.test.HasVersionNoFieldJDO;
import org.datanucleus.test.HasVersionWithFieldJDO;
import org.datanucleus.test.KitchenSink;

/**
 * @author Max Ross <maxr@google.com>
 */
public class JDOInsertionTest extends JDOTestCase {

  private static final String DEFAULT_VERSION_PROPERTY_NAME = "OPT_VERSION";

  public void testSimpleInsert() throws EntityNotFoundException {
    Flight f1 = new Flight();
    f1.setOrigin("BOS");
    f1.setDest("MIA");
    f1.setMe(2);
    f1.setYou(4);
    f1.setName("Harold");
    assertNull(f1.getId());
    pm.makePersistent(f1);
    assertNotNull(f1.getId());
    Entity entity = ldth.ds.get(KeyFactory.decodeKey(f1.getId()));
    assertNotNull(entity);
    assertEquals("BOS", entity.getProperty("origin"));
    assertEquals("MIA", entity.getProperty("dest"));
    assertEquals("Harold", entity.getProperty("name"));
    assertEquals(2L, entity.getProperty("me"));
    assertEquals(4L, entity.getProperty("you"));
    assertEquals(1L, entity.getProperty(DEFAULT_VERSION_PROPERTY_NAME));
    assertEquals(Flight.class.getSimpleName(), entity.getKind());
  }

  public void testSimpleInsertWithNamedKey() throws EntityNotFoundException {
    Flight f = new Flight();
    f.setId("foo");
    assertNotNull(f.getId());
    pm.makePersistent(f);
    Entity entity = ldth.ds.get(KeyFactory.decodeKey(f.getId()));
    assertNotNull(entity);
    assertEquals("foo", entity.getKey().getName());
  }

  public void testKitchenSinkInsert() throws EntityNotFoundException {
    KitchenSink ks = KitchenSink.newKitchenSink();
    assertNull(ks.key);
    pm.makePersistent(ks);
    assertNotNull(ks.key);

    Entity entity = ldth.ds.get(KeyFactory.decodeKey(ks.key));
    assertNotNull(entity);
    assertEquals(KitchenSink.class.getSimpleName(), entity.getKind());

    Entity sameEntity = KitchenSink.newKitchenSinkEntity(KeyFactory.decodeKey(ks.key));
    assertEquals(sameEntity.getProperties(), entity.getProperties());
  }

  public void testVersionInserts() throws EntityNotFoundException {
    HasVersionNoFieldJDO hv = new HasVersionNoFieldJDO();
    pm.makePersistent(hv);

    Entity entity = ldth.ds.get(KeyFactory.decodeKey(hv.getId()));
    assertNotNull(entity);
    assertEquals(1L, entity.getProperty("myversioncolumn"));

    HasVersionWithFieldJDO hvwf = new HasVersionWithFieldJDO();
    pm.makePersistent(hvwf);
    entity = ldth.ds.get(KeyFactory.decodeKey(hvwf.getId()));
    assertNotNull(entity);
    assertEquals(1L, entity.getProperty(DEFAULT_VERSION_PROPERTY_NAME));
    assertEquals(1L, hvwf.getVersion());
  }

//  public void testEmbeddable() throws EntityNotFoundException {
//    Person p = new Person();
//    p.setDob(new Date());
//    p.setName(new Name());
//    p.getName().setFirstName("jimmy");
//    p.getName().setSurName("jam");
//    pm.makePersistent(p);
//
//    assertNotNull(p.getId());
//
//    Entity entity = ldth.ds.get(KeyFactory.decodeKey(p.getId()));
//    assertNotNull(entity);
//  }
}
