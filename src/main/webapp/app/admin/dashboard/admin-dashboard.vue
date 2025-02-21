<template>
  <div class="table-responsive">
    <b-progress
      v-for="location in locations"
      :key="location.id"
      :max="location.amount"
      height="2rem"
      :striped="true"
      class="border border-primary mb-2"
    >
      <b-progress-bar :value="location.booked">
        <span class="p-4"
          >{{ location.location }}: <strong>{{ location.booked }} / {{ location.amount }}</strong></span
        >
      </b-progress-bar>
    </b-progress>

    <div class="table-responsive">
      <table class="table table-striped" aria-describedby="booths">
        <thead>
          <tr>
            <th scope="row"><span>Firma</span></th>
            <th scope="row"><span>Rechnungsanschrift</span></th>
            <th scope="row"><span>Logo</span></th>
            <th scope="row"><span>Telefonnummer</span></th>
            <th scope="row"><span>Kurzbeschreibung</span></th>
            <th scope="row"><span>Stand</span></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="checklist in checklists" :key="checklist.id" data-cy="entityTable">
            <td>{{ checklist.companyName }}</td>
            <td
              :class="{ 'list-group-item-success': checklist.address, 'list-group-item-danger': !checklist.address }"
              v-text="checklist.address ? 'Ja' : 'Nein'"
            ></td>
            <td
              :class="{ 'list-group-item-success': checklist.logo, 'list-group-item-warning': !checklist.logo }"
              v-text="checklist.logo ? 'Ja' : 'Nein'"
            ></td>
            <td
              :class="{ 'list-group-item-success': checklist.phoneNumber, 'list-group-item-warning': !checklist.phoneNumber }"
              v-text="checklist.phoneNumber ? 'Ja' : 'Nein'"
            ></td>
            <td
              :class="{ 'list-group-item-success': checklist.companyDescription, 'list-group-item-warning': !checklist.companyDescription }"
              v-text="checklist.companyDescription ? 'Ja' : 'Nein'"
            ></td>
            <td>{{ checklist.booth }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>

  <Button class="btn btn-primary">Rechnungsdaten</Button>
</template>

<script lang="ts" src="./admin-dashboard.component.ts"></script>
