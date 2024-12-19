/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import CompanyUpdate from './company-update.vue';
import CompanyService from './company.service';
import AlertService from '@/shared/alert/alert.service';

import DepartmentService from '@/entities/department/department.service';

type CompanyUpdateComponentType = InstanceType<typeof CompanyUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const companySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<CompanyUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Company Management Update Component', () => {
    let comp: CompanyUpdateComponentType;
    let companyServiceStub: SinonStubbedInstance<CompanyService>;

    beforeEach(() => {
      route = {};
      companyServiceStub = sinon.createStubInstance<CompanyService>(CompanyService);
      companyServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          companyService: () => companyServiceStub,
          departmentService: () =>
            sinon.createStubInstance<DepartmentService>(DepartmentService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(CompanyUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.company = companySample;
        companyServiceStub.update.resolves(companySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(companyServiceStub.update.calledWith(companySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        companyServiceStub.create.resolves(entity);
        const wrapper = shallowMount(CompanyUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.company = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(companyServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        companyServiceStub.find.resolves(companySample);
        companyServiceStub.retrieve.resolves([companySample]);

        // WHEN
        route = {
          params: {
            companyId: '' + companySample.id,
          },
        };
        const wrapper = shallowMount(CompanyUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.company).toMatchObject(companySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        companyServiceStub.find.resolves(companySample);
        const wrapper = shallowMount(CompanyUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
