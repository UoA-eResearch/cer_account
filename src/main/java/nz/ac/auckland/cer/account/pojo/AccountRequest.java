package nz.ac.auckland.cer.account.pojo;

public class AccountRequest {

    private String fullName;
    private String preferredName;
    private String institution;
    private String otherInstitution;
    private String division;
    private String department;
    private String phone;
    private String email;
    private Integer institutionalRoleId;
    private String otherInstitutionalRole;

    public String getPreferredName() {

        return preferredName;
    }

    public void setPreferredName(
            String preferredName) {

        this.preferredName = preferredName;
    }

    public String getFullName() {

        return fullName;
    }

    public void setFullName(
            String fullName) {

        this.fullName = fullName;
    }

    public String getInstitution() {

        return institution;
    }

    public void setInstitution(
            String institution) {

        this.institution = institution;
    }

    public String getDivision() {

        return division;
    }

    public void setDivision(
            String division) {

        this.division = division;
    }

    public String getDepartment() {

        return department;
    }

    public void setDepartment(
            String department) {

        this.department = department;
    }

    public String getPhone() {

        return phone;
    }

    public void setPhone(
            String phone) {

        this.phone = phone;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(
            String email) {

        this.email = email;
    }

    public Integer getInstitutionalRoleId() {

        return institutionalRoleId;
    }

    public void setInstitutionalRoleId(
            Integer institutionalRoleId) {

        this.institutionalRoleId = institutionalRoleId;
    }

    public String getOtherInstitution() {

        return otherInstitution;
    }

    public void setOtherInstitution(
            String otherInstitution) {

        this.otherInstitution = otherInstitution;
    }

    public String getOtherInstitutionalRole() {

        return otherInstitutionalRole;
    }

    public void setOtherInstitutionalRole(
            String otherInstitutionalRole) {

        this.otherInstitutionalRole = otherInstitutionalRole;
    }

}
