import axios from 'axios';

const baseApiUrl = 'api/waitinglist';

export default class WaitingListService {
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

  public notifyWaitingList(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/send-emails`)
        .then(() => {
          resolve();
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

  public updateWaitingListStatus(id: number, status: boolean): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${id}`, { waitingList: status })
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
