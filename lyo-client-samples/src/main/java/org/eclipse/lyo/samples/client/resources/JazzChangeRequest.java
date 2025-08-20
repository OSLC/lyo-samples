package org.eclipse.lyo.samples.client.resources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.lyo.oslc.domains.Oslc_cmVocabularyConstants;
import org.eclipse.lyo.oslc.domains.Oslc_qmVocabularyConstants;
import org.eclipse.lyo.oslc.domains.cm.ChangeRequest;
import org.eclipse.lyo.oslc.domains.cm.Oslc_cmDomainConstants;
import org.eclipse.lyo.oslc4j.core.annotation.OslcAllowedValue;
import org.eclipse.lyo.oslc4j.core.annotation.OslcDescription;
import org.eclipse.lyo.oslc4j.core.annotation.OslcName;
import org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace;
import org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition;
import org.eclipse.lyo.oslc4j.core.annotation.OslcRange;
import org.eclipse.lyo.oslc4j.core.annotation.OslcReadOnly;
import org.eclipse.lyo.oslc4j.core.annotation.OslcResourceShape;
import org.eclipse.lyo.oslc4j.core.annotation.OslcTitle;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.OslcConstants;

@OslcNamespace(Oslc_cmDomainConstants.CHANGEREQUEST_NAMESPACE)
@OslcName(Oslc_cmDomainConstants.CHANGEREQUEST_LOCALNAME)
@OslcResourceShape(title = "ChangeRequest Shape", describes = Oslc_cmDomainConstants.CHANGEREQUEST_TYPE)
public class JazzChangeRequest extends ChangeRequest {

    private Set<String> dctermsTypes = new HashSet<>();
    private final Set<Link> testedByTestCases = new HashSet<>();

    public void setDctermsTypes(final String[] dctermsTypes) {
        this.dctermsTypes.clear();

        if (dctermsTypes != null) {
            this.dctermsTypes.addAll(Arrays.asList(dctermsTypes));
        }
    }

    @OslcAllowedValue({"Defect", "Task", "Story", "Bug Report", "Feature Request"})
    @OslcDescription("A short string representation for the type, example 'Defect'.")
    @OslcName("type")
    @OslcPropertyDefinition(OslcConstants.DCTERMS_NAMESPACE + "type")
    @OslcTitle("Types")
    public String[] getDctermsTypes() {
        return dctermsTypes.toArray(new String[dctermsTypes.size()]);
    }

    @OslcDescription("Test case by which this change request is tested.")
    @OslcName("testedByTestCase")
    @OslcPropertyDefinition(Oslc_cmVocabularyConstants.CHANGE_MANAGEMENT_VOCAB_NAMSPACE + "testedByTestCase")
    @OslcRange(Oslc_qmVocabularyConstants.TYPE_TESTCASE)
    @OslcReadOnly(false)
    @OslcTitle("Tested by Test Cases")
    public Link[] getTestedByTestCases() {
        return testedByTestCases.toArray(new Link[testedByTestCases.size()]);
    }

    public void setTestedByTestCases(final Link[] testedByTestCases) {
        this.testedByTestCases.clear();

        if (testedByTestCases != null) {
            this.testedByTestCases.addAll(Arrays.asList(testedByTestCases));
        }
    }
}
