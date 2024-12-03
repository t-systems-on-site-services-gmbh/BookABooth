package de.tsystems.onsite.bookabooth.domain;

import static de.tsystems.onsite.bookabooth.domain.CompanyTestSamples.*;
import static de.tsystems.onsite.bookabooth.domain.DepartmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import de.tsystems.onsite.bookabooth.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mytest")
class DepartmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Department.class);
        Department department1 = getDepartmentSample1();
        Department department2 = new Department();
        assertThat(department1).isNotEqualTo(department2);

        department2.setId(department1.getId());
        assertThat(department1).isEqualTo(department2);

        department2 = getDepartmentSample2();
        assertThat(department1).isNotEqualTo(department2);
    }

    @Test
    void companyTest() throws Exception {
        Department department = getDepartmentRandomSampleGenerator();
        Company companyBack = getCompanyRandomSampleGenerator();

        department.addCompany(companyBack);
        assertThat(department.getCompanies()).containsOnly(companyBack);

        department.removeCompany(companyBack);
        assertThat(department.getCompanies()).doesNotContain(companyBack);

        department.companies(new HashSet<>(Set.of(companyBack)));
        assertThat(department.getCompanies()).containsOnly(companyBack);

        department.setCompanies(new HashSet<>());
        assertThat(department.getCompanies()).doesNotContain(companyBack);
    }
}
