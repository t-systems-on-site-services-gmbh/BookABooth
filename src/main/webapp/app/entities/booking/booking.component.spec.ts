/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Booking from './booking.vue';
import BookingService from './booking.service';
import AlertService from '@/shared/alert/alert.service';

type BookingComponentType = InstanceType<typeof Booking>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Booking Management Component', () => {
    let bookingServiceStub: SinonStubbedInstance<BookingService>;
    let mountOptions: MountingOptions<BookingComponentType>['global'];

    beforeEach(() => {
      bookingServiceStub = sinon.createStubInstance<BookingService>(BookingService);
      bookingServiceStub.retrieve.resolves({ headers: {} });

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
          bookingService: () => bookingServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        bookingServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Booking, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(bookingServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.bookings[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: BookingComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Booking, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        bookingServiceStub.retrieve.reset();
        bookingServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        bookingServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeBooking();
        await comp.$nextTick(); // clear components

        // THEN
        expect(bookingServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(bookingServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
