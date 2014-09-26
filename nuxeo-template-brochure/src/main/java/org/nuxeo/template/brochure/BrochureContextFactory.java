package org.nuxeo.template.brochure;


import java.util.Map;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.template.api.context.ContextExtensionFactory;
import org.nuxeo.template.api.context.DocumentWrapper;

public class BrochureContextFactory implements ContextExtensionFactory {

    @Override
    public Object getExtension(DocumentModel currentDocument,
            DocumentWrapper wrapper, Map<String, Object> ctx) {
        return new BrochureContext(currentDocument, wrapper, ctx);
    }

}
