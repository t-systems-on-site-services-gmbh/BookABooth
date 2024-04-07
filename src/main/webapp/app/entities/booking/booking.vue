<template>
  <div>
    <h2 id="page-heading" data-cy="BookingHeading">
      <span id="booking-heading">Bookings</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
        <router-link :to="{ name: 'BookingCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-booking"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Booking erstellen</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && bookings && bookings.length === 0">
      <span>Keine Bookings gefunden</span>
    </div>
    <div class="table-responsive" v-if="bookings && bookings.length > 0">
      <table class="table table-striped" aria-describedby="bookings">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Received</span></th>
            <th scope="row"><span>Status</span></th>
            <th scope="row"><span>Company</span></th>
            <th scope="row"><span>Booth</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="booking in bookings" :key="booking.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BookingView', params: { bookingId: booking.id } }">{{ booking.id }}</router-link>
            </td>
            <td>{{ formatDateShort(booking.received) || '' }}</td>
            <td>{{ booking.status }}</td>
            <td>
              <div v-if="booking.company">
                <router-link :to="{ name: 'CompanyView', params: { companyId: booking.company.id } }">{{ booking.company.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="booking.booth">
                <router-link :to="{ name: 'BoothView', params: { boothId: booking.booth.id } }">{{ booking.booth.id }}</router-link>
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
                <router-link
                  :to="{ name: 'BookingEdit', params: { bookingId: booking.id } }"
                  class="btn btn-primary btn-sm edit"
                  data-cy="entityEditButton"
                >
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline">Bearbeiten</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(booking)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Löschen</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="bookaboothApp.booking.delete.question" data-cy="bookingDeleteDialogHeading">Löschen bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-booking-heading">Soll Booking {{ removeId }} wirklich dauerhaft gelöscht werden?</p>
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
            Löschen
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./booking.component.ts"></script>
