/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import ServicePackage from './service-package.vue';
import ServicePackageService from './service-package.service';
import AlertService from '@/shared/alert/alert.service';

type ServicePackageComponentType = InstanceType<typeof ServicePackage>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('ServicePackage Management Component', () => {
    let servicePackageServiceStub: SinonStubbedInstance<ServicePackageService>;
    let mountOptions: MountingOptions<ServicePackageComponentType>['global'];

    beforeEach(() => {
      servicePackageServiceStub = sinon.createStubInstance<ServicePackageService>(ServicePackageService);
      servicePackageServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          servicePackageService: () => servicePackageServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        servicePackageServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(ServicePackage, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(servicePackageServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.servicePackages[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: ServicePackageComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(ServicePackage, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        servicePackageServiceStub.retrieve.reset();
        servicePackageServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        servicePackageServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeServicePackage();
        await comp.$nextTick(); // clear components

        // THEN
        expect(servicePackageServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(servicePackageServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
