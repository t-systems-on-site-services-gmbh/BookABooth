import axios from 'axios';

import { type IBooking } from '@/shared/model/booking.model';

const baseApiUrl = 'api/bookings';

export default class BookingService {
  public find(id: number): Promise<IBooking> {
    return new Promise<IBooking>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieve(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieveMyBooking(): Promise<IBooking> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/mybooking`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieveUnavailableBooths(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/unavailable`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public delete(id: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public cancel(id: number): Promise<IBooking> {
    return new Promise<IBooking>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/cancel/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public create(boothId: number): Promise<IBooking> {
    return new Promise<IBooking>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/booth/${boothId}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public confirm(bookingId: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/confirm/${bookingId}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public update(entity: IBooking): Promise<IBooking> {
    return new Promise<IBooking>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public partialUpdate(entity: IBooking): Promise<IBooking> {
    return new Promise<IBooking>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
