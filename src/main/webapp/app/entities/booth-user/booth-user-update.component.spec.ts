/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import BoothUserUpdate from './booth-user-update.vue';
import BoothUserService from './booth-user.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import CompanyService from '@/entities/company/company.service';

type BoothUserUpdateComponentType = InstanceType<typeof BoothUserUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const boothUserSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<BoothUserUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('BoothUser Management Update Component', () => {
    let comp: BoothUserUpdateComponentType;
    let boothUserServiceStub: SinonStubbedInstance<BoothUserService>;

    beforeEach(() => {
      route = {};
      boothUserServiceStub = sinon.createStubInstance<BoothUserService>(BoothUserService);
      boothUserServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          boothUserService: () => boothUserServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          companyService: () =>
            sinon.createStubInstance<CompanyService>(CompanyService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(BoothUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(BoothUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.boothUser = boothUserSample;
        boothUserServiceStub.update.resolves(boothUserSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(boothUserServiceStub.update.calledWith(boothUserSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        boothUserServiceStub.create.resolves(entity);
        const wrapper = shallowMount(BoothUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.boothUser = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(boothUserServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        boothUserServiceStub.find.resolves(boothUserSample);
        boothUserServiceStub.retrieve.resolves([boothUserSample]);

        // WHEN
        route = {
          params: {
            boothUserId: '' + boothUserSample.id,
          },
        };
        const wrapper = shallowMount(BoothUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.boothUser).toMatchObject(boothUserSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        boothUserServiceStub.find.resolves(boothUserSample);
        const wrapper = shallowMount(BoothUserUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
