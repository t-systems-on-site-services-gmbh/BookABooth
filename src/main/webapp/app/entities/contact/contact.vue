<template>
  <div>
    <h2 id="page-heading" data-cy="ContactHeading">
      <span id="contact-heading">Contacts</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
        <router-link :to="{ name: 'ContactCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-contact"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Contact erstellen</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && contacts && contacts.length === 0">
      <span>Keine Contacts gefunden</span>
    </div>
    <div class="table-responsive" v-if="contacts && contacts.length > 0">
      <table class="table table-striped" aria-describedby="contacts">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>First Name</span></th>
            <th scope="row"><span>Last Name</span></th>
            <th scope="row"><span>Mail</span></th>
            <th scope="row"><span>Phone</span></th>
            <th scope="row"><span>Responsibility</span></th>
            <th scope="row"><span>Note</span></th>
            <th scope="row"><span>Company</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="contact in contacts" :key="contact.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ContactView', params: { contactId: contact.id } }">{{ contact.id }}</router-link>
            </td>
            <td>{{ contact.firstName }}</td>
            <td>{{ contact.lastName }}</td>
            <td>{{ contact.mail }}</td>
            <td>{{ contact.phone }}</td>
            <td>{{ contact.responsibility }}</td>
            <td>{{ contact.note }}</td>
            <td>
              <span v-for="(company, i) in contact.companies" :key="company.id"
                >{{ i > 0 ? ', ' : '' }}
                <router-link class="form-control-static" :to="{ name: 'CompanyView', params: { companyId: company.id } }">{{
                  company.id
                }}</router-link>
              </span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ContactView', params: { contactId: contact.id } }"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Details</span>
                </router-link>
                <router-link
                  :to="{ name: 'ContactEdit', params: { contactId: contact.id } }"
                  class="btn btn-primary btn-sm edit"
                  data-cy="entityEditButton"
                >
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline">Bearbeiten</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(contact)"
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
        <span id="bookaboothApp.contact.delete.question" data-cy="contactDeleteDialogHeading">Löschen bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-contact-heading">Soll Contact {{ removeId }} wirklich dauerhaft gelöscht werden?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Abbrechen</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-contact"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeContact()"
          >
            Löschen
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./contact.component.ts"></script>
