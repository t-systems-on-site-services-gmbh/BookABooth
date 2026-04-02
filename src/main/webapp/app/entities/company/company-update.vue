<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.company.home.createOrEditLabel" data-cy="CompanyCreateUpdateHeading">Firma erstellen oder bearbeiten</h2>
        <div>
          <div class="form-group" v-if="company.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="company.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="company-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-mail">E-Mail</label>
            <input
              type="text"
              class="form-control"
              name="mail"
              id="company-mail"
              data-cy="mail"
              :class="{ valid: !v$.mail.$invalid, invalid: v$.mail.$invalid }"
              v-model="v$.mail.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-billingAddressRow1">Adresszeile 1</label>
            <input
              type="text"
              class="form-control"
              name="billingAddressRow1"
              id="company-billingAddressRow1"
              data-cy="billingAddressRow1"
              :class="{ valid: !v$.billingAddressRow1.$invalid, invalid: v$.billingAddressRow1.$invalid }"
              v-model="v$.billingAddressRow1.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-billingAddressRow2">Adresszeile 2</label>
            <input
              type="text"
              class="form-control"
              name="billingAddressRow2"
              id="company-billingAddressRow2"
              data-cy="billingAddressRow2"
              :class="{ valid: !v$.billingAddressRow2.$invalid, invalid: v$.billingAddressRow2.$invalid }"
              v-model="v$.billingAddressRow2.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-billingAddressRow3">Adresszeile 3</label>
            <input
              type="text"
              class="form-control"
              name="billingAddressRow3"
              id="company-billingAddressRow3"
              data-cy="billingAddressRow3"
              :class="{ valid: !v$.billingAddressRow3.$invalid, invalid: v$.billingAddressRow3.$invalid }"
              v-model="v$.billingAddressRow3.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-billingAddressRow4">Adresszeile 4</label>
            <input
              type="text"
              class="form-control"
              name="billingAddressRow4"
              id="company-billingAddressRow4"
              data-cy="billingAddressRow4"
              :class="{ valid: !v$.billingAddressRow4.$invalid, invalid: v$.billingAddressRow4.$invalid }"
              v-model="v$.billingAddressRow4.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-billingZipCode">PLZ</label>
            <input
              type="text"
              class="form-control"
              name="billingZipCode"
              id="company-billingZipCode"
              data-cy="billingZipCode"
              :class="{ valid: !v$.billingZipCode.$invalid, invalid: v$.billingZipCode.$invalid }"
              v-model="v$.billingZipCode.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-billingCity">Ort</label>
            <input
              type="text"
              class="form-control"
              name="billingCity"
              id="company-billingCity"
              data-cy="billingCity"
              :class="{ valid: !v$.billingCity.$invalid, invalid: v$.billingCity.$invalid }"
              v-model="v$.billingCity.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-logo">Logo</label>
            <input
              type="file"
              class="form-control"
              name="logo"
              id="company-logo"
              accept="image/*"
              @change="onLogoChange"
            />
            <img v-if="logoPreview" :src="logoPreview" alt="Logo preview" style="margin-top: 10px; max-height: 150px;" />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-description">Beschreibung</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="company-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-waitingList">Warteliste</label>
            <input
              type="checkbox"
              class="form-check"
              name="waitingList"
              id="company-waitingList"
              data-cy="waitingList"
              :class="{ valid: !v$.waitingList.$invalid, invalid: v$.waitingList.$invalid }"
              v-model="v$.waitingList.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-exhibitorList">Ausstellerliste</label>
            <input
              type="checkbox"
              class="form-check"
              name="exhibitorList"
              id="company-exhibitorList"
              data-cy="exhibitorList"
              :class="{ valid: !v$.exhibitorList.$invalid, invalid: v$.exhibitorList.$invalid }"
              v-model="v$.exhibitorList.$model"
            />
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
<script lang="ts" src="./company-update.component.ts"></script>
