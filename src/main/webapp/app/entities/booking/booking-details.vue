<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="booking">
        <h2 class="jh-entity-heading" data-cy="bookingDetailsHeading"><span>Buchung</span> {{ booking.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Eingegangen</span>
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
            <span>Unternehmen</span>
          </dt>
          <dd>
            <div v-if="booking.company">
              <router-link :to="{ name: 'CompanyView', params: { companyId: booking.company.id } }">{{
                companies.find(l => l.id == booking.company.id)?.name
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Stand</span>
          </dt>
          <dd>
            <div v-if="booking.booth">
              <router-link :to="{ name: 'BoothView', params: { boothId: booking.booth.id } }">{{
                booths.find(l => l.id == booking.booth.id)?.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./booking-details.component.ts"></script>
