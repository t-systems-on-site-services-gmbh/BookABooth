import axios from 'axios';

const baseApiUrl = 'api/waitinglist';

export default class CompanyService {
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
    return axios
      .patch(`${baseApiUrl}/${id}`, { waitingList: status })
      .then(response => {
        console.log(`Erfolgreich aktualisiert:`, response.data);
        return response.data;
      })
      .catch(error => {
        console.error(`Fehler beim Aktualisieren des Status für ID: ${id}`, error);
        throw error; // Stelle sicher, dass Fehler korrekt weitergeleitet werden
      });
  }
}
