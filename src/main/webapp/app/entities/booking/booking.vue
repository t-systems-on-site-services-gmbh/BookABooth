<template>
  <div>
    <h2 id="page-heading" data-cy="BookingHeading">
      <span id="booking-heading">Buchungen</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
      </div>
    </h2>
    <br />
    <div class="d-flex justify-content-end mb-3">
      <input type="search" class="form-control" placeholder="Nach Unternehmen suchen" v-model="searchQuery" />
    </div>
    <div class="alert alert-warning" v-if="!isFetching && bookings && filteredBookings.length === 0">
      <span>Keine Buchungen gefunden</span>
    </div>
    <div class="table-responsive" v-if="bookings && filteredBookings.length > 0">
      <table class="table table-striped" aria-describedby="bookings">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Eingegangen</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Unternehmen</span></th>
            <th scope="row"><span>Stand</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="booking in filteredBookings" :key="booking.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BookingView', params: { bookingId: booking.id } }">{{ booking.id }}</router-link>
            </td>
            <td>{{ formatDateShort(booking.received) || '' }}</td>
            <td>{{ booking.status }}</td>
            <td>
              <div v-if="booking.company">
                <router-link :to="{ name: 'CompanyView', params: { companyId: booking.company.id } }">{{
                  companies.find(l => l.id == booking.company.id)?.name
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="booking.booth">
                <router-link :to="{ name: 'BoothView', params: { boothId: booking.booth.id } }">{{
                  booths.find(l => l.id == booking.booth.id)?.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'BookingView', params: { bookingId: booking.id } }"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Details</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(booking)"
                  variant="danger"
                  class="btn btn-sm"
                  :disabled="booking.status === 'CANCELED'"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Stornieren</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="bookaboothApp.booking.delete.question" data-cy="bookingDeleteDialogHeading">Stornierung bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-booking-heading">
          Wollen Sie die Standbuchung {{ removeId }} wirklich stornieren? Die Stornierung kann nicht rückgängig gemacht werden.<br />Der
          Aussteller wird per E-Mail benachrichtigt und, wenn nötig, von der Ausstellerliste entfernt.
        </p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Abbrechen</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-booking"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeBooking()"
          >
            Stornieren
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./booking.component.ts"></script>
