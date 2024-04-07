import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const System = () => import('@/entities/system/system.vue');
const SystemUpdate = () => import('@/entities/system/system-update.vue');
const SystemDetails = () => import('@/entities/system/system-details.vue');

const Booth = () => import('@/entities/booth/booth.vue');
const BoothUpdate = () => import('@/entities/booth/booth-update.vue');
const BoothDetails = () => import('@/entities/booth/booth-details.vue');

const Location = () => import('@/entities/location/location.vue');
const LocationUpdate = () => import('@/entities/location/location-update.vue');
const LocationDetails = () => import('@/entities/location/location-details.vue');

const Company = () => import('@/entities/company/company.vue');
const CompanyUpdate = () => import('@/entities/company/company-update.vue');
const CompanyDetails = () => import('@/entities/company/company-details.vue');

const Department = () => import('@/entities/department/department.vue');
const DepartmentUpdate = () => import('@/entities/department/department-update.vue');
const DepartmentDetails = () => import('@/entities/department/department-details.vue');

const ServicePackage = () => import('@/entities/service-package/service-package.vue');
const ServicePackageUpdate = () => import('@/entities/service-package/service-package-update.vue');
const ServicePackageDetails = () => import('@/entities/service-package/service-package-details.vue');

const Booking = () => import('@/entities/booking/booking.vue');
const BookingUpdate = () => import('@/entities/booking/booking-update.vue');
const BookingDetails = () => import('@/entities/booking/booking-details.vue');

const BoothUser = () => import('@/entities/booth-user/booth-user.vue');
const BoothUserUpdate = () => import('@/entities/booth-user/booth-user-update.vue');
const BoothUserDetails = () => import('@/entities/booth-user/booth-user-details.vue');

const Contact = () => import('@/entities/contact/contact.vue');
const ContactUpdate = () => import('@/entities/contact/contact-update.vue');
const ContactDetails = () => import('@/entities/contact/contact-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'system',
      name: 'System',
      component: System,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'system/new',
      name: 'SystemCreate',
      component: SystemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'system/:systemId/edit',
      name: 'SystemEdit',
      component: SystemUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'system/:systemId/view',
      name: 'SystemView',
      component: SystemDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booth',
      name: 'Booth',
      component: Booth,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booth/new',
      name: 'BoothCreate',
      component: BoothUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booth/:boothId/edit',
      name: 'BoothEdit',
      component: BoothUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booth/:boothId/view',
      name: 'BoothView',
      component: BoothDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location',
      name: 'Location',
      component: Location,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/new',
      name: 'LocationCreate',
      component: LocationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/:locationId/edit',
      name: 'LocationEdit',
      component: LocationUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'location/:locationId/view',
      name: 'LocationView',
      component: LocationDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'company',
      name: 'Company',
      component: Company,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'company/new',
      name: 'CompanyCreate',
      component: CompanyUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'company/:companyId/edit',
      name: 'CompanyEdit',
      component: CompanyUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'company/:companyId/view',
      name: 'CompanyView',
      component: CompanyDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'department',
      name: 'Department',
      component: Department,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'department/new',
      name: 'DepartmentCreate',
      component: DepartmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'department/:departmentId/edit',
      name: 'DepartmentEdit',
      component: DepartmentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'department/:departmentId/view',
      name: 'DepartmentView',
      component: DepartmentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'service-package',
      name: 'ServicePackage',
      component: ServicePackage,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'service-package/new',
      name: 'ServicePackageCreate',
      component: ServicePackageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'service-package/:servicePackageId/edit',
      name: 'ServicePackageEdit',
      component: ServicePackageUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'service-package/:servicePackageId/view',
      name: 'ServicePackageView',
      component: ServicePackageDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booking',
      name: 'Booking',
      component: Booking,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booking/new',
      name: 'BookingCreate',
      component: BookingUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booking/:bookingId/edit',
      name: 'BookingEdit',
      component: BookingUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booking/:bookingId/view',
      name: 'BookingView',
      component: BookingDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booth-user',
      name: 'BoothUser',
      component: BoothUser,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booth-user/new',
      name: 'BoothUserCreate',
      component: BoothUserUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booth-user/:boothUserId/edit',
      name: 'BoothUserEdit',
      component: BoothUserUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'booth-user/:boothUserId/view',
      name: 'BoothUserView',
      component: BoothUserDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'contact',
      name: 'Contact',
      component: Contact,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'contact/new',
      name: 'ContactCreate',
      component: ContactUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'contact/:contactId/edit',
      name: 'ContactEdit',
      component: ContactUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'contact/:contactId/view',
      name: 'ContactView',
      component: ContactDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
