import axios from 'axios';

import { type ISystem } from '@/shared/model/system.model';

const baseApiUrl = 'api/systems';

export default class SystemService {
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

  public partialUpdate(entity: ISystem): Promise<ISystem> {
    return new Promise<ISystem>((resolve, reject) => {
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
