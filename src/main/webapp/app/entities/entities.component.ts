import { defineComponent, provide } from 'vue';

import SystemService from './system/system.service';
import BoothService from './booth/booth.service';
import LocationService from './location/location.service';
import CompanyService from './company/company.service';
import DepartmentService from './department/department.service';
import ServicePackageService from './service-package/service-package.service';
import BookingService from './booking/booking.service';
import BoothUserService from './booth-user/booth-user.service';
import ContactService from './contact/contact.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('systemService', () => new SystemService());
    provide('boothService', () => new BoothService());
    provide('locationService', () => new LocationService());
    provide('companyService', () => new CompanyService());
    provide('departmentService', () => new DepartmentService());
    provide('servicePackageService', () => new ServicePackageService());
    provide('bookingService', () => new BookingService());
    provide('boothUserService', () => new BoothUserService());
    provide('contactService', () => new ContactService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
