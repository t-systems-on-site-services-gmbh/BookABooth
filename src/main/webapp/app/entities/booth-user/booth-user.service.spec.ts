/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import BoothUserService from './booth-user.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { BoothUser } from '@/shared/model/booth-user.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('BoothUser Service', () => {
    let service: BoothUserService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new BoothUserService();
      currentDate = new Date();
      elemDefault = new BoothUser(123, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', currentDate, currentDate, false);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            verified: dayjs(currentDate).format(DATE_TIME_FORMAT),
            lastLogin: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a BoothUser', async () => {
        const returnedFromService = Object.assign(
          {
            id: 123,
            verified: dayjs(currentDate).format(DATE_TIME_FORMAT),
            lastLogin: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            verified: currentDate,
            lastLogin: currentDate,
          },
          returnedFromService,
        );

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a BoothUser', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a BoothUser', async () => {
        const returnedFromService = Object.assign(
          {
            phone: 'BBBBBB',
            note: 'BBBBBB',
            verificationCode: 'BBBBBB',
            verified: dayjs(currentDate).format(DATE_TIME_FORMAT),
            lastLogin: dayjs(currentDate).format(DATE_TIME_FORMAT),
            disabled: true,
          },
          elemDefault,
        );

        const expected = Object.assign(
          {
            verified: currentDate,
            lastLogin: currentDate,
          },
          returnedFromService,
        );
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a BoothUser', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a BoothUser', async () => {
        const patchObject = Object.assign(
          {
            verificationCode: 'BBBBBB',
            lastLogin: dayjs(currentDate).format(DATE_TIME_FORMAT),
            disabled: true,
          },
          new BoothUser(),
        );
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            verified: currentDate,
            lastLogin: currentDate,
          },
          returnedFromService,
        );
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a BoothUser', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of BoothUser', async () => {
        const returnedFromService = Object.assign(
          {
            phone: 'BBBBBB',
            note: 'BBBBBB',
            verificationCode: 'BBBBBB',
            verified: dayjs(currentDate).format(DATE_TIME_FORMAT),
            lastLogin: dayjs(currentDate).format(DATE_TIME_FORMAT),
            disabled: true,
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            verified: currentDate,
            lastLogin: currentDate,
          },
          returnedFromService,
        );
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve().then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of BoothUser', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a BoothUser', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a BoothUser', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
