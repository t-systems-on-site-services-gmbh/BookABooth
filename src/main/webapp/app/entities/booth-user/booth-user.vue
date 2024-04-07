<template>
  <div>
    <h2 id="page-heading" data-cy="BoothUserHeading">
      <span id="booth-user-heading">Booth Users</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
        <router-link :to="{ name: 'BoothUserCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-booth-user"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Booth User erstellen</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && boothUsers && boothUsers.length === 0">
      <span>Keine Booth Users gefunden</span>
    </div>
    <div class="table-responsive" v-if="boothUsers && boothUsers.length > 0">
      <table class="table table-striped" aria-describedby="boothUsers">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Phone</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Verification Code</span></th>
            <th scope="row"><span>Verified</span></th>
            <th scope="row"><span>Last Login</span></th>
            <th scope="row"><span>Disabled</span></th>
            <th scope="row"><span>User</span></th>
            <th scope="row"><span>Company</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="boothUser in boothUsers" :key="boothUser.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'BoothUserView', params: { boothUserId: boothUser.id } }">{{ boothUser.id }}</router-link>
            </td>
            <td>{{ boothUser.phone }}</td>
            <td>{{ boothUser.note }}</td>
            <td>{{ boothUser.verificationCode }}</td>
            <td>{{ formatDateShort(boothUser.verified) || '' }}</td>
            <td>{{ formatDateShort(boothUser.lastLogin) || '' }}</td>
            <td>{{ boothUser.disabled }}</td>
            <td>
              {{ boothUser.user ? boothUser.user.id : '' }}
            </td>
            <td>
              <div v-if="boothUser.company">
                <router-link :to="{ name: 'CompanyView', params: { companyId: boothUser.company.id } }">{{
                  boothUser.company.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'BoothUserView', params: { boothUserId: boothUser.id } }"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Details</span>
                </router-link>
                <router-link
                  :to="{ name: 'BoothUserEdit', params: { boothUserId: boothUser.id } }"
                  class="btn btn-primary btn-sm edit"
                  data-cy="entityEditButton"
                >
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline">Bearbeiten</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(boothUser)"
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
        <span id="bookaboothApp.boothUser.delete.question" data-cy="boothUserDeleteDialogHeading">Löschen bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-boothUser-heading">Soll Booth User {{ removeId }} wirklich dauerhaft gelöscht werden?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Abbrechen</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-boothUser"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeBoothUser()"
          >
            Löschen
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./booth-user.component.ts"></script>
