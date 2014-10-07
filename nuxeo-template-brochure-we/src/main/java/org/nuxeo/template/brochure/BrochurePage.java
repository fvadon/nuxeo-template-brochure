/**
 *
 */

package org.nuxeo.template.brochure;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.template.api.adapters.TemplateBasedDocument;

import java.util.Iterator;



/**
 * The root entry for the WebEngine module.
 * @author fvadon
 */
@Path("/brochure")
@Produces("text/html;charset=UTF-8")
@WebObject(type="BrochurePage")
public class BrochurePage extends ModuleRoot {

    public static final String RENDITION_NAME = "Handbook word template";

    @GET
    public Object doGet() {
        CoreSession session = ctx.getCoreSession();

        String query = "SELECT * FROM Document WHERE ecm:currentLifeCycleState != 'deleted' and ecm:primaryType='Handbook' and ecm:isProxy=0 and ecm:isVersion=0";
        DocumentModelList availableHandbooks = session.query(query);

        return getView("index").arg("availableHandbooks", availableHandbooks);
    }

    @GET
    @Path("{id}")
    public Object getHandbookSubjects(@PathParam("id") String id){
        //Getting the list of all subjects for the list of all parts
        CoreSession session = ctx.getCoreSession();
        DocumentRef handbookRef = new IdRef(id);
        DocumentModel handbook = session.getDocument(handbookRef);
        List<String> subjects = new ArrayList<String>();
        List<String> arrayParts = Arrays.asList((String[])handbook.getPropertyValue("handbook:block_ref"));
        Iterator<String> iteratorParts=arrayParts.iterator();
        String partId;
        DocumentModel partModel;
        Iterator<String> iteratorSubjects;
        String subject;
        while (iteratorParts.hasNext()){
            partId=iteratorParts.next();
            partModel = session.getDocument(new IdRef(partId));
            iteratorSubjects=Arrays.asList((String[])partModel.getPropertyValue("dc:subjects")).iterator();
            while(iteratorSubjects.hasNext()){
                subject=iteratorSubjects.next();
                subjects.remove(subject);//To make sure it's only put once in the list.
                subjects.add(subject);
            }

        }
        return getView("handbookSubjects").arg("subjects",subjects).arg("id",id);

    }

    @POST
    @Path("submit/{id}")
    @Produces({"application/pdf","application/msword"})
    public Object submitForm(@PathParam("id") String id) throws Exception {

        FormData data = getContext().getForm();
        String[] selectedSubject = data.getList("subject");
        CoreSession session = ctx.getCoreSession();
        DocumentRef handbookRef = new IdRef(id);
        DocumentModel handbook = session.getDocument(handbookRef);

        // Modify the handbook to store the subject on it without saving it.
        handbook.setProperty("dublincore", "subjects", selectedSubject);
        TemplateBasedDocument template = handbook.getAdapter(TemplateBasedDocument.class);
        Blob rendition = template.renderWithTemplate(RENDITION_NAME);
        ResponseBuilder resp = Response.ok(rendition).header("Cache-Control",
                "public").header("Server", "Nuxeo/WebEngine-1.0");

        resp.type(rendition.getMimeType());
        return resp.build();

        //return rendition;
        //return redirect(getPath() + "/success");


    }
    @GET
    @Path("success")
    public Object viewSuccess() throws UnsupportedEncodingException {
        return getView("success");
    }

}
