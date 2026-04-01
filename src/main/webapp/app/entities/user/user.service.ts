import axios from 'axios';

import { type IUser } from '@/shared/model/user.model';

const baseApiUrl = 'api/admin/users';

export default class UserService {
  public find(id: number): Promise<IUser> {
    return new Promise<IUser>((resolve, reject) => {
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

  public create(entity: IUser): Promise<IUser> {
    return new Promise<IUser>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public update(entity: IUser): Promise<IUser> {
    return new Promise<IUser>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.login}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public partialUpdate(entity: IUser): Promise<IUser> {
    return new Promise<IUser>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.login}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public uploadImage(entity: String, contentBase64: any): Promise<IUser> {
    return new Promise<IUser>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/${entity}/image`, contentBase64, {
          headers: { 'Content-Type': 'text/plain; charset=x-user-defined-binary' },
        })
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
