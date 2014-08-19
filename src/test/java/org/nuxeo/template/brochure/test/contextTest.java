/**
 *
 */

package org.nuxeo.template.brochure.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.template.tests.SimpleTemplateDocTestCase;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;




import org.nuxeo.template.api.adapters.TemplateBasedDocument;
import org.nuxeo.template.api.adapters.TemplateSourceDocument;

import com.google.inject.Inject;

/**
 * @author fvadon
 */

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
/*@Deploy({
	"studio.extensions.brochure-nuxeo-presales",
	//"org.nuxeo.ecm.platform.convert",
	//"org.nuxeo.ecm.platform.rendition.api",
	//"org.nuxeo.template.manager",
	//"org.nuxeo.ecm.platform.mimetype.api",
	//"org.nuxeo.ecm.platform.mimetype.core",
	//"org.nuxeo.ecm.core.api",
	//"org.nuxeo.ecm.core",
    //"org.nuxeo.ecm.core.schema",
    //"org.nuxeo.ecm.core.event",
    //"org.nuxeo.ecm.platform.dublincore",
    //"org.nuxeo.template.manager.api",
	})*/
public class contextTest extends SimpleTemplateDocTestCase{

    @Inject
    CoreSession coreSession;

    protected static final String TEMPLATE_NAME = "mytestTemplate";


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("studio.extensions.brochure-nuxeo-presales");

    }


    @Override
    protected TemplateBasedDocument setupTestDocs() throws Exception {

        //Create the parent Brochure, will have template associated later
        DocumentModel Brochure = coreSession.createDocumentModel("/", "Brochure",
                "Brochure");
        Assert.assertNotNull(Brochure);
        Brochure = coreSession.createDocument(Brochure);
        Assert.assertNotNull(Brochure);

        // Create a few parts with different type of criteria.
        DocumentModel Part = coreSession.createDocumentModel("/Brochure/", "Part_intro",
                "Part");
        Part.setPropertyValue("dc:title","Intro");
        Part.setPropertyValue("part:text_content", "I am a text content");
        Assert.assertNotNull(Part);
        Part = coreSession.createDocument(Part);
        Assert.assertNotNull(Part);
        Assert.assertEquals("I am a text content",Part.getPropertyValue("part:text_content"));

        Part = coreSession.createDocumentModel("/Brochure/", "Part_sales",
                "Part");
        Part.setPropertyValue("dc:title","part for sales");
        Part.setPropertyValue("part:text_content", "I am a part for sales");
        Part.setPropertyValue("part:criteria", "sales_group");
        Assert.assertNotNull(Part);
        Part = coreSession.createDocument(Part);
        Assert.assertNotNull(Part);
        Assert.assertEquals("sales_group",Part.getPropertyValue("part:criteria"));

        Part = coreSession.createDocumentModel("/Brochure/", "Part_marketing",
                "Part");
        Part.setPropertyValue("dc:title","part for marketing");
        Part.setPropertyValue("part:text_content", "I am a part for marketing");
        Part.setPropertyValue("part:criteria", "marketing_group");
        Assert.assertNotNull(Part);
        Part = coreSession.createDocument(Part);
        Assert.assertNotNull(Part);
        Assert.assertEquals("marketing_group",Part.getPropertyValue("part:criteria"));

        // create template
        DocumentModel templateDoc = coreSession.createDocumentModel("/", "templatedDoc", "TemplateSource");
        templateDoc.setProperty("dublincore", "title", "MyTemplate");
        templateDoc.setPropertyValue("tmpl:templateName", TEMPLATE_NAME);
        Blob fileBlob = getTemplateBlob();
        templateDoc.setProperty("file", "content", fileBlob);
        templateDoc = coreSession.createDocument(templateDoc);

        TemplateSourceDocument templateSource = templateDoc.getAdapter(TemplateSourceDocument.class);
        Assert.assertNotNull(templateSource);
        Assert.assertEquals(TEMPLATE_NAME, templateSource.getName());
        // associate doc and template
        TemplateBasedDocument adapter = Brochure.getAdapter(TemplateBasedDocument.class);
        Assert.assertNotNull(adapter);

        return adapter;


        /*
        DocumentModel root = session.getRootDocument();

        // create template
        DocumentModel templateDoc = session.createDocumentModel(
                root.getPathAsString(), "templatedDoc", "TemplateSource");
        templateDoc.setProperty("dublincore", "title", "MyTemplate");
        templateDoc.setPropertyValue("tmpl:templateName", TEMPLATE_NAME);
        Blob fileBlob = getTemplateBlob();
        templateDoc.setProperty("file", "content", fileBlob);
        templateDoc = session.createDocument(templateDoc);

        TemplateSourceDocument templateSource = templateDoc.getAdapter(TemplateSourceDocument.class);
        assertNotNull(templateSource);
        assertEquals(TEMPLATE_NAME, templateSource.getName());

        // now create a template based doc
        DocumentModel testDoc = session.createDocumentModel(
                root.getPathAsString(), "templatedBasedDoc",
                "TemplateBasedFile");
        testDoc.setProperty("dublincore", "title", "MyTestDoc");
        testDoc.setProperty("dublincore", "description", "some description");

        // set dc:subjects
        List<String> subjects = new ArrayList<String>();
        subjects.add("Subject 1");
        subjects.add("Subject 2");
        subjects.add("Subject 3");
        testDoc.setPropertyValue("dc:subjects", (Serializable) subjects);

        // add an image as first entry of files
        File imgFile = FileUtils.getResourceFileFromContext("data/android.jpg");
        Blob imgBlob = new FileBlob(imgFile);
        imgBlob.setFilename("android.jpg");
        List<Map<String, Serializable>> blobs = new ArrayList<Map<String, Serializable>>();
        Map<String, Serializable> blob1 = new HashMap<String, Serializable>();
        blob1.put("file", (Serializable) imgBlob);
        blob1.put("filename", "android.jpg");
        blobs.add(blob1);
        testDoc.setPropertyValue("files:files", (Serializable) blobs);

        testDoc = session.createDocument(testDoc);

        // associate doc and template
        TemplateBasedDocument adapter = testDoc.getAdapter(TemplateBasedDocument.class);
        assertNotNull(adapter);

        adapter.setTemplate(templateDoc, true);

        return adapter; */
    }

    @Test
    public void testgetChildrenPartDocForCurrentUser() throws Exception {
        TemplateBasedDocument Brochure = setupTestDocs();
        Assert.assertNotNull(Brochure);

    }

    @Override
    protected Blob getTemplateBlob() {
        File file = FileUtils.getResourceFileFromContext("data/test.ftl");
        Blob fileBlob = new FileBlob(file);
        fileBlob.setFilename("test.ftl");
        return fileBlob;
    }

}
