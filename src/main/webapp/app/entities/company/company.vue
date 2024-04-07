<template>
  <div>
    <h2 id="page-heading" data-cy="CompanyHeading">
      <span id="company-heading">Companies</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Liste aktualisieren</span>
        </button>
        <router-link :to="{ name: 'CompanyCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-company"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span>Company erstellen</span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && companies && companies.length === 0">
      <span>Keine Companies gefunden</span>
    </div>
    <div class="table-responsive" v-if="companies && companies.length > 0">
      <table class="table table-striped" aria-describedby="companies">
        <thead>
          <tr>
            <th scope="row"><span>ID</span></th>
            <th scope="row"><span>Name</span></th>
            <th scope="row"><span>Mail</span></th>
            <th scope="row"><span>Billing Address</span></th>
            <th scope="row"><span>Logo</span></th>
            <th scope="row"><span>Description</span></th>
            <th scope="row"><span>Waiting List</span></th>
            <th scope="row"><span>Exhibitor List</span></th>
            <th scope="row"><span>Department</span></th>
            <th scope="row"><span>Contact</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="company in companies" :key="company.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CompanyView', params: { companyId: company.id } }">{{ company.id }}</router-link>
            </td>
            <td>{{ company.name }}</td>
            <td>{{ company.mail }}</td>
            <td>{{ company.billingAddress }}</td>
            <td>{{ company.logo }}</td>
            <td>{{ company.description }}</td>
            <td>{{ company.waitingList }}</td>
            <td>{{ company.exhibitorList }}</td>
            <td>
              <span v-for="(department, i) in company.departments" :key="department.id"
                >{{ i > 0 ? ', ' : '' }}
                <router-link class="form-control-static" :to="{ name: 'DepartmentView', params: { departmentId: department.id } }">{{
                  department.id
                }}</router-link>
              </span>
            </td>
            <td>
              <span v-for="(contact, i) in company.contacts" :key="contact.id"
                >{{ i > 0 ? ', ' : '' }}
                <router-link class="form-control-static" :to="{ name: 'ContactView', params: { contactId: contact.id } }">{{
                  contact.id
                }}</router-link>
              </span>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'CompanyView', params: { companyId: company.id } }"
                  class="btn btn-info btn-sm details"
                  data-cy="entityDetailsButton"
                >
                  <font-awesome-icon icon="eye"></font-awesome-icon>
                  <span class="d-none d-md-inline">Details</span>
                </router-link>
                <router-link
                  :to="{ name: 'CompanyEdit', params: { companyId: company.id } }"
                  class="btn btn-primary btn-sm edit"
                  data-cy="entityEditButton"
                >
                  <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                  <span class="d-none d-md-inline">Bearbeiten</span>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(company)"
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
        <span id="bookaboothApp.company.delete.question" data-cy="companyDeleteDialogHeading">Löschen bestätigen</span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-company-heading">Soll Company {{ removeId }} wirklich dauerhaft gelöscht werden?</p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Abbrechen</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-company"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeCompany()"
          >
            Löschen
          </button>
        </div>
      </template>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./company.component.ts"></script>
