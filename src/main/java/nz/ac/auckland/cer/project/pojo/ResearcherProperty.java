package nz.ac.auckland.cer.project.pojo;

public class ResearcherProperty {

    private Integer id;
    private Integer researcherId;
    private String timestamp;
    private String propname;
    private String propvalue;
    private Integer siteId;
    private String siteName;

    public Integer getId() {

        return id;
    }

    public void setId(
            Integer id) {

        this.id = id;
    }

    public Integer getResearcherId() {

        return researcherId;
    }

    public void setResearcherId(
            Integer researcherId) {

        this.researcherId = researcherId;
    }

    public String getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(
            String timestamp) {

        this.timestamp = timestamp;
    }

    public String getPropname() {

        return propname;
    }

    public void setPropname(
            String propname) {

        this.propname = propname;
    }

    public String getPropvalue() {

        return propvalue;
    }

    public void setPropvalue(
            String propvalue) {

        this.propvalue = propvalue;
    }

    public Integer getSiteId() {

        return siteId;
    }

    public void setSiteId(
            Integer siteId) {

        this.siteId = siteId;
    }

    public String getSiteName() {

        return siteName;
    }

    public void setSiteName(
            String siteName) {

        this.siteName = siteName;
    }

}
