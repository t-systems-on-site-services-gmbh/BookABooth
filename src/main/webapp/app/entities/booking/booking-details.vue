<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="booking">
        <h2 class="jh-entity-heading" data-cy="bookingDetailsHeading"><span>Booking</span> {{ booking.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Received</span>
          </dt>
          <dd>
            <span v-if="booking.received">{{ formatDateLong(booking.received) }}</span>
          </dd>
          <dt>
            <span>Status</span>
          </dt>
          <dd>
            <span>{{ booking.status }}</span>
          </dd>
          <dt>
            <span>Company</span>
          </dt>
          <dd>
            <div v-if="booking.company">
              <router-link :to="{ name: 'CompanyView', params: { companyId: booking.company.id } }">{{ booking.company.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Booth</span>
          </dt>
          <dd>
            <div v-if="booking.booth">
              <router-link :to="{ name: 'BoothView', params: { boothId: booking.booth.id } }">{{ booking.booth.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link v-if="booking.id" :to="{ name: 'BookingEdit', params: { bookingId: booking.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Bearbeiten</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./booking-details.component.ts"></script>
