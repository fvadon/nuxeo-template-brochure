/**
 *
 */

package org.nuxeo.template.brochure.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import javax.security.auth.login.LoginContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.UserService;
import org.nuxeo.template.adapters.doc.TemplateBasedDocumentAdapterImpl;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;
import org.nuxeo.template.api.adapters.TemplateSourceDocument;

import com.google.inject.Inject;

/**
 * @author fvadon
 */

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@Deploy({
	"studio.extensions.brochure-nuxeo-presales",
	"org.nuxeo.template.manager.api",
	"org.nuxeo.template.manager",
	"org.nuxeo.ecm.platform.convert",
	"org.nuxeo.ecm.platform.rendition.core",
	"org.nuxeo.template.manager",
    "org.nuxeo.template.manager.api",
    "org.nuxeo.ecm.platform.commandline.executor",
    "org.nuxeo.ecm.platform.login",
    "org.nuxeo.ecm.platform.web.common",
	})
@LocalDeploy({"org.nuxeo.template.brochure:OSGI-INF/extensions/brochure-context-contrib.xml"})
public class contextTest {

    @Inject
    CoreSession coreSession;

    @Inject
    RepositoryManager rm;

    protected static final String TEMPLATE_NAME = "mytestTemplate";

    protected static final String SALES_USER = "salesUser";
    protected static final String MARKETING_USER = "MarketingUser";
    protected static final String SALES_GROUP = "sales_group";
    protected static final String MARKETING_GROUP = "marketing_group";



    protected UserManager userManager;

    protected UserService userService;

    @Before
    public void setUp() throws Exception {

    }


    protected TemplateBasedDocument setupTestDocs() throws Exception {

        //Create the parent Brochure, will have template associated later
        DocumentModel brochure = coreSession.createDocumentModel("/", "Brochure",
                "Brochure");
        Assert.assertNotNull(brochure);
        brochure = coreSession.createDocument(brochure);
        Assert.assertNotNull(brochure);

        // Create a few parts with different type of group.
        DocumentModel part = coreSession.createDocumentModel("/Brochure/", "Part_intro",
                "Part");
        part.setPropertyValue("dc:title","Intro");
        part.setPropertyValue("part:text_content", "I am a text content");
        Assert.assertNotNull(part);
        part = coreSession.createDocument(part);
        Assert.assertNotNull(part);
        Assert.assertEquals("I am a text content",part.getPropertyValue("part:text_content"));

        part = coreSession.createDocumentModel("/Brochure/", "Part_sales",
                "Part");
        part.setPropertyValue("dc:title","part for sales");
        part.setPropertyValue("part:text_content", "I am a part for sales");
        part.setPropertyValue("part:group", SALES_GROUP);
        Assert.assertNotNull(part);
        part = coreSession.createDocument(part);
        Assert.assertNotNull(part);
        Assert.assertEquals(SALES_GROUP,part.getPropertyValue("part:group"));

        part = coreSession.createDocumentModel("/Brochure/", "Part_marketing",
                "Part");
        part.setPropertyValue("dc:title","part for marketing");
        part.setPropertyValue("part:text_content", "I am a part for marketing");
        part.setPropertyValue("part:group", MARKETING_GROUP);
        Assert.assertNotNull(part);
        part = coreSession.createDocument(part);
        Assert.assertNotNull(part);
        Assert.assertEquals(MARKETING_GROUP,part.getPropertyValue("part:group"));


     // giving permission to everyone on the doc
        ACPImpl acp = new ACPImpl();
        ACLImpl acl = new ACLImpl("local");
        ACE /*ace = new ACE(SALES_USER, "Read", true);
        acl.add(ace); */
        ace = new ACE("Everyone", "ReadWrite", true);
        acl.add(ace);
        acp.addACL(acl);
        coreSession.setACP(brochure.getRef(), acp, false);

        // create template
        DocumentModel templateDoc = coreSession.createDocumentModel("/", "templatedDoc", "TemplateSource");
        templateDoc.setProperty("dublincore", "title", "MyTemplate");
        templateDoc.setPropertyValue("tmpl:templateName", TEMPLATE_NAME);
        Blob fileBlob = getTemplateBlob();
        templateDoc.setProperty("file", "content", fileBlob);
        templateDoc = coreSession.createDocument(templateDoc);
        coreSession.setACP(templateDoc.getRef(), acp, false);


        TemplateSourceDocument templateSource = templateDoc.getAdapter(TemplateSourceDocument.class);
        Assert.assertNotNull(templateSource);
        Assert.assertEquals(TEMPLATE_NAME, templateSource.getName());

     // associate doc and template
        brochure.addFacet(TemplateBasedDocumentAdapterImpl.TEMPLATEBASED_FACET);
        TemplateBasedDocument adapter = brochure.getAdapter(TemplateBasedDocument.class);
        Assert.assertNotNull(adapter);
        adapter.setTemplate(templateDoc, true);

        return adapter;

    }

    @Test
    public void testgetChildrenPartDocForCurrentUser() throws Exception {


        setUpUsers();

        TemplateBasedDocument brochureAdapter = setupTestDocs();
        Assert.assertNotNull(brochureAdapter);


        DocumentModel brochure = brochureAdapter.getAdaptedDoc();
        assertNotNull(brochure);

        DocumentRef brochureRef = brochure.getRef();

        coreSession.save();
        LoginContext loginContext = Framework.loginAsUser(SALES_USER);

        CoreSession session = CoreInstance.openCoreSession(rm.getDefaultRepositoryName());

        brochure = session.getDocument(brochureRef);
        brochureAdapter = brochure.getAdapter(TemplateBasedDocument.class);

        String processorType = brochureAdapter.getSourceTemplate(TEMPLATE_NAME).getTemplateType();
        assertEquals("Freemarker", processorType);

        Blob newBlob = brochureAdapter.renderWithTemplate(TEMPLATE_NAME);

        String xmlContent = newBlob.getString();

        assertEquals("text/html", newBlob.getMimeType());
        assertTrue(newBlob.getFilename().endsWith(".html"));

        assertTrue(xmlContent.contains(brochure.getId()));
        assertTrue(xmlContent.contains(SALES_USER));

        assertTrue(xmlContent.contains("I am a part for sales"));
        assertTrue(!xmlContent.contains("I am a part for marketing"));
        session.close();
        loginContext.logout();





    }

    protected Blob getTemplateBlob() {
        File file = FileUtils.getResourceFileFromContext("data/test.ftl");
        Blob fileBlob = new FileBlob(file);
        fileBlob.setFilename("test.ftl");
        return fileBlob;
    }

    protected void setUpUsers() {

        userService = (UserService) Framework.getRuntime().getComponent(
                UserService.NAME);

        userManager = userService.getUserManager();

        //Set up the groups

        DocumentModel newGroup = userManager.getBareGroupModel();
        newGroup.setProperty("group", "groupname", SALES_GROUP);
        userManager.createGroup(newGroup);

        newGroup.setProperty("group", "groupname", MARKETING_GROUP);
        userManager.createGroup(newGroup);


        // Setting up a user in a specific group

        DocumentModel salesUser = userManager.getBareUserModel();
        salesUser.setProperty("user", "username", SALES_USER);
        salesUser.setProperty("user", "groups", Arrays.asList(SALES_GROUP));

        userManager.createUser(salesUser);

        NuxeoPrincipal salesUserPrincipal = userManager.getPrincipal(SALES_USER);
        assertNotNull(salesUserPrincipal);
        assertEquals(SALES_USER, salesUserPrincipal.getName());
        assertTrue(salesUserPrincipal.isMemberOf(SALES_GROUP));



    }

}
