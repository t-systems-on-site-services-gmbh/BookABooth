<template>
  <div class="container-fluid">
    <div class="row">
      <div>
        <a class="btn btn-primary m-3" @click="downloadExcel">Rechnungsdaten</a>
        <a :href="countAllProfiles === 0 ? null : bccForAllUsers" class="btn btn-primary m-3"
          >Mail an <br />
          Alle Firmen<br />
          <span class="badge badge-light">{{ countAllProfiles }}</span>
        </a>
        <a :href="countIncompleteProfiles === 0 ? null : bccForAllUsersWithIncompleteProfile" class="btn btn-primary m-3"
          >Mail an <br />
          Unvollständige Profile<br />
          <span class="badge badge-light">{{ countIncompleteProfiles }}</span></a
        >
      </div>
      <div>
        <system class="m-3"></system>
      </div>
    </div>
    <div v-for="location in locations">
      <span
        >{{ location.location }}: <strong>{{ location.booked }} / {{ location.amount }}</strong></span
      >
      <b-progress :key="location.id" :max="location.amount" height="2rem" :striped="true" class="border border-primary mb-2">
        <b-progress-bar :value="location.booked"> </b-progress-bar>
      </b-progress>
    </div>

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
              :class="{ 'list-group-item-success': checklist.logo, 'list-group-item-danger': !checklist.logo }"
              v-text="checklist.logo ? 'Ja' : 'Nein'"
            ></td>
            <td
              :class="{ 'list-group-item-success': checklist.phoneNumber, 'list-group-item-danger': !checklist.phoneNumber }"
              v-text="checklist.phoneNumber ? 'Ja' : 'Nein'"
            ></td>
            <td
              :class="{ 'list-group-item-success': checklist.companyDescription, 'list-group-item-danger': !checklist.companyDescription }"
              v-text="checklist.companyDescription ? 'Ja' : 'Nein'"
            ></td>
            <td>
              <span v-if="checklist.booth" class="badge badge-pill badge-success">{{ checklist.booth }}</span>
              <div v-if="checklist.canceledBooth">
                <span
                  v-for="(canceled, index) in checklist.canceledBooth.split(',')"
                  :key="index"
                  class="badge badge-pill badge-danger mr-1"
                >
                  {{ canceled.trim() }}
                </span>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts" src="./admin-dashboard.component.ts"></script>
