/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Company from './company.vue';
import CompanyService from './company.service';
import AlertService from '@/shared/alert/alert.service';

type CompanyComponentType = InstanceType<typeof Company>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Company Management Component', () => {
    let companyServiceStub: SinonStubbedInstance<CompanyService>;
    let mountOptions: MountingOptions<CompanyComponentType>['global'];

    beforeEach(() => {
      companyServiceStub = sinon.createStubInstance<CompanyService>(CompanyService);
      companyServiceStub.retrieve.resolves({ headers: {} });

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
          companyService: () => companyServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        companyServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Company, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(companyServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.companies[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: CompanyComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Company, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        companyServiceStub.retrieve.reset();
        companyServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        companyServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeCompany();
        await comp.$nextTick(); // clear components

        // THEN
        expect(companyServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(companyServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
