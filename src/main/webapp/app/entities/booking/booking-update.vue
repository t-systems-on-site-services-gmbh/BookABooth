<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.booking.home.createOrEditLabel" data-cy="BookingCreateUpdateHeading">Booking erstellen oder bearbeiten</h2>
        <div>
          <div class="form-group" v-if="booking.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="booking.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booking-received">Received</label>
            <div class="d-flex">
              <input
                id="booking-received"
                data-cy="received"
                type="datetime-local"
                class="form-control"
                name="received"
                :class="{ valid: !v$.received.$invalid, invalid: v$.received.$invalid }"
                :value="convertDateTimeFromServer(v$.received.$model)"
                @change="updateZonedDateTimeField('received', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booking-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="booking-status"
              data-cy="status"
            >
              <option v-for="bookingStatus in bookingStatusValues" :key="bookingStatus" v-bind:value="bookingStatus">
                {{ bookingStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booking-company">Company</label>
            <select class="form-control" id="booking-company" data-cy="company" name="company" v-model="booking.company" required>
              <option v-if="!booking.company" v-bind:value="null" selected></option>
              <option
                v-bind:value="booking.company && companyOption.id === booking.company.id ? booking.company : companyOption"
                v-for="companyOption in companies"
                :key="companyOption.id"
              >
                {{ companyOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.company.$anyDirty && v$.company.$invalid">
            <small class="form-text text-danger" v-for="error of v$.company.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booking-booth">Booth</label>
            <select class="form-control" id="booking-booth" data-cy="booth" name="booth" v-model="booking.booth" required>
              <option v-if="!booking.booth" v-bind:value="null" selected></option>
              <option
                v-bind:value="booking.booth && boothOption.id === booking.booth.id ? booking.booth : boothOption"
                v-for="boothOption in booths"
                :key="boothOption.id"
              >
                {{ boothOption.id }}
              </option>
            </select>
          </div>
          <div v-if="v$.booth.$anyDirty && v$.booth.$invalid">
            <small class="form-text text-danger" v-for="error of v$.booth.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Abbrechen</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Speichern</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./booking-update.component.ts"></script>
