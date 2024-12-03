<template>
  <div>
    <div class="row justify-content-center">
      <div class="col-md-8 toastify-container">
        <h2 v-if="username" id="settings-title">
          <span>
            Profil von Benutzer [<strong>{{ username }}</strong
            >]
          </span>
        </h2>

        <div class="alert alert-success" role="alert" v-if="success">
          <strong>Einstellungen wurden gespeichert!</strong>
        </div>

        <div class="alert alert-danger" role="alert" v-if="errorEmailExists">
          <strong>E-Mail-Adresse wird bereits verwendet!</strong> Bitte wählen Sie eine andere aus.
        </div>

        <form name="form" id="settings-form" role="form" @submit.prevent="save()" v-if="settingsAccount" novalidate>
          <div class="accordion" id="accordionProfile">
            <div class="card" v-if="!hasAnyAuthority('ROLE_ADMIN')">
              <div class="card-header" Id="headingOne">
                <h4 class="mb-0">
                  <!--- <button class="btn btn-link btn-block text-left" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne"></button> -->
                  Unternehmensdaten
                </h4>
              </div>
              <div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordionProfile">
                <div class="card-body">
                  <!--- Unternehmenslogo Previewfenster -->
                  <div class="form-group">
                    <img
                      v-if="settingsAccount.company?.logo"
                      alt="Unternehmenslogo"
                      :key="componentKey"
                      :src="absoluteImageUrl"
                      style="width: 150px; height: auto"
                    />
                  </div>
                  <!--- Name des Unternehmens -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.name">Firmenname</label>
                    <input
                      type="text"
                      class="form-control"
                      id="company.name"
                      name="company.name"
                      placeholder="Name Ihres Unternehmens"
                      :class="{ valid: !v$.settingsAccount.company.name.$invalid, invalid: v$.settingsAccount.company.name.$invalid }"
                      v-model="v$.settingsAccount.company.name.$model"
                      minlength="1"
                      maxlength="100"
                      required
                      data-cy="company.name"
                    />
                    <div v-if="v$.settingsAccount.company.name.$anyDirty && v$.settingsAccount.company.name.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.name.required"
                        >Ein Firmenname wird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.name.minLength"
                        >Ihr Firmenname muss mindestens 1 Zeichen lang sein.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.name.maxLength"
                        >Ihr Firmenname darf nicht länger als 100 Zeichen sein.</small
                      >
                    </div>
                  </div>
                  <!--- Adresse des Unternehmens -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.billingAddress">Rechnungsanschrift</label>
                    <textarea
                      id="company.billingAddress"
                      name="company.billingAddress"
                      class="form-control"
                      placeholder="Rechnungsanschrift Ihres Unternehmens"
                      :class="{
                        valid: !v$.settingsAccount.company.billingAddress.$invalid,
                        invalid: v$.settingsAccount.company.billingAddress.$invalid,
                      }"
                      v-model="v$.settingsAccount.company.billingAddress.$model"
                      minlength="1"
                      maxlength="254"
                      required
                      data-cy="company.billingaddress"
                    >
                    </textarea>
                    <div v-if="v$.settingsAccount.company.billingAddress.$anyDirty && v$.settingsAccount.company.billingAddress.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingAddress.required"
                        >Eine Rechnungsanschrift wird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingAddress.minLength"
                        >Ihre Rechnungsanschrift muss mindestens 1 Zeichen lang sein.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.company.billingAddress.maxLength"
                        >Ihre Rechnungsanschrift darf nicht länger als 254 Zeichen sein.</small
                      >
                    </div>
                  </div>
                  <!--- Kurzbeschreibung des Unternehmens -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.description"
                      >Kurze Beschreibung Ihres Unternehmens (max. 254 Zeichen)</label
                    >
                    <textarea
                      id="company.description"
                      name="company.description"
                      class="form-control"
                      placeholder="Beschreibung Ihres Unternehmens"
                      :class="{
                        valid: !v$.settingsAccount.company.description.$invalid,
                        invalid: v$.settingsAccount.company.description.$invalid,
                      }"
                      v-model="v$.settingsAccount.company.description.$model"
                      minlength="0"
                      maxlength="254"
                      data-cy="company.description"
                    >
                    </textarea>
                    <div v-if="v$.settingsAccount.company.description.$invalid">
                      <small class="form-text text-danger" v-if="v$.settingsAccount.company.description.maxLength"
                        >Ihre Firmenbeschreibung darf nicht länger als 254 Zeichen sein.</small
                      >
                    </div>
                  </div>
                  <!--- Unternehmenslogo hochladen / aktualisieren -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.logoUpload">
                      <p v-if="settingsAccount.company?.logo">Aktualisieren Sie hier Ihr Unternehmenslogo</p>
                      <p v-else>Laden Sie hier Ihr Unternehmenslogo hoch</p>
                    </label>
                    <input
                      type="file"
                      ref="file"
                      class="form-control-file"
                      id="company.logoUpload"
                      name="company.logoUpload"
                      :class="{
                        valid: !v$.settingsAccount.company.logoUpload.$invalid,
                        invalid: v$.settingsAccount.company.logoUpload.$invalid,
                      }"
                      data-cy="company.logoupload"
                      accept="image/*"
                      @change="logoUpload"
                    />
                  </div>
                  <!--- Checkbox um Freigabe in Ausstellerliste zu widerrufen -->
                  <div class="form-group">
                    <label class="form-control-label" for="company.exhibitorList"
                      >Hier können Sie Ihre Freigabe in der Austellerliste verwalten</label
                    >
                    <br />
                    <input
                      type="checkbox"
                      id="company.exhibitorList"
                      name="company.exhibitorList"
                      :class="{
                        valid: !v$.settingsAccount.company.exhibitorList.$invalid,
                        invalid: v$.settingsAccount.company.exhibitorList.$invalid,
                      }"
                      v-model="v$.settingsAccount.company.exhibitorList.$model"
                      data-cy="exhibitorlist"
                    />
                    {{ onExhibitorList }}
                  </div>
                </div>
              </div>
            </div>
            <div class="card">
              <div class="card-header" id="headingTwo">
                <h4 class="mb-0">
                  <!--- <button class="btn btn-link btn-block text-left" type="button" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true" aria-controls="collapseTwo"></button> -->
                  Benutzerdaten
                </h4>
              </div>
              <div id="collapseTwo" class="collapse show" aria-labelledby="headingTwo" data-parent="#accordionProfile">
                <div class="card-body">
                  <!--- Vorname des Ansprechpartners / Accountbesitzers -->
                  <div class="form-group">
                    <label class="form-control-label" for="user.firstName">Vorname Ansprechpartner</label>
                    <input
                      type="text"
                      class="form-control"
                      id="user.firstName"
                      name="user.firstName"
                      placeholder="Ihr Vorname"
                      :class="{ valid: !v$.settingsAccount.user.firstName.$invalid, invalid: v$.settingsAccount.user.firstName.$invalid }"
                      v-model="v$.settingsAccount.user.firstName.$model"
                      minlength="1"
                      maxlength="50"
                      required
                      data-cy="user.firstname"
                    />
                    <div v-if="v$.settingsAccount.user.firstName.$anyDirty && v$.settingsAccount.user.firstName.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.firstName.required"
                        >Ihr Vorname wird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.firstName.minLength"
                        >Ihr Vorname muss mindestens 1 Zeichen lang sein</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.firstName.maxLength"
                        >Ihr Vorname darf nicht länger als 50 Zeichen sein</small
                      >
                    </div>
                  </div>
                  <!--- Nachname des Ansprechpartners / Accountbesitzers -->
                  <div class="form-group">
                    <label class="form-control-label" for="user.lastName">Nachname Ansprechpartner</label>
                    <input
                      type="text"
                      class="form-control"
                      id="user.lastName"
                      name="user.lastName"
                      placeholder="Ihr Nachname"
                      :class="{ valid: !v$.settingsAccount.user.lastName.$invalid, invalid: v$.settingsAccount.user.lastName.$invalid }"
                      v-model="v$.settingsAccount.user.lastName.$model"
                      minlength="1"
                      maxlength="50"
                      required
                      data-cy="user.lastname"
                    />
                    <div v-if="v$.settingsAccount.user.lastName.$anyDirty && v$.settingsAccount.user.lastName.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.lastName.required"
                        >Ihr Nachname wird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.lastName.minLength"
                        >Ihr Nachname muss mindestens 1 Zeichen lang sein</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.lastName.maxLength"
                        >Ihr Nachname darf nicht länger als 50 Zeichen sein</small
                      >
                    </div>
                  </div>
                  <!--- E-Mail-Adresse des Ansprechpartners -->
                  <div class="form-group">
                    <label class="form-control-label" for="user.email">E-Mail-Adresse</label>
                    <input
                      type="email"
                      class="form-control"
                      id="user.email"
                      name="user.email"
                      placeholder="Ihre E-Mail-Adresse"
                      :class="{ valid: !v$.settingsAccount.user.email.$invalid, invalid: v$.settingsAccount.user.email.$invalid }"
                      v-model="v$.settingsAccount.user.email.$model"
                      minlength="5"
                      maxlength="254"
                      email
                      required
                      data-cy="user.email"
                    />
                    <div v-if="v$.settingsAccount.user.email.$anyDirty && v$.settingsAccount.user.email.$invalid">
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.email.required"
                        >Ihre E-Mail-Adressewird benötigt.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.email.email"
                        >Ihre E-Mail-Adresse istungültig.</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.email.minLength">
                        Ihre E-Mail-Adresse muss mindestens 5 Zeichen lang sein</small
                      >
                      <small class="form-text text-danger" v-if="!v$.settingsAccount.user.email.maxLength"
                        >Ihre E-Mail-Adresse darf nicht länger als 50 Zeichen sein</small
                      >
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <br />
          <button type="submit" :disabled="v$.settingsAccount.$invalid" class="btn btn-primary" data-cy="submit">Speichern</button>
        </form>
        <br />
        <div class="form-group" v-if="!hasAnyAuthority('ROLE_ADMIN')">
          <!--- Aktueller Buchungsstaus -->
          <div>
            <p v-if="settingsAccount?.booking?.status">
              Ihr aktueller Buchungsstatus: <strong>{{ settingsAccount.booking?.status }}</strong>
            </p>
            <p v-else>Ihr aktueller Buchungsstaus: <strong>Keine Buchung vorhanden</strong></p>
          </div>
          <!--- Standbuchung bestätigen -->
          <p v-if="settingsAccount?.booking?.status === 'PREBOOKED'">Sie können Ihre vorgemerkte Standbuchung hier bestätigen.</p>
          <p v-else>Sie haben im Moment keine vorgemerkte Buchung</p>
          <button
            type="button"
            class="btn btn-primary"
            :disabled="settingsAccount?.booking?.status != 'PREBOOKED'"
            @click="confirmBooking"
            data-cy="confirmbooking"
          >
            Standbuchung bestätigen
          </button>
          <br /><br />
          <!--- Von Warteliste entfernen -->
          <p v-if="settingsAccount?.company?.waitingList">
            Sie befinden sich aktuell auf der Warteliste. Wenn Sie sich davon entfernen möchten, nutzen Sie den folgenden Button.
          </p>
          <p v-else>
            Sie befinden sich im Moment nicht auf der Warteliste.<br />
            Unter "Ihre Buchung" können Sie sich zur Warteliste hinzufügen.
          </p>
          <button
            type="button"
            class="btn btn-primary"
            :disabled="!settingsAccount?.company?.waitingList"
            @click="removeWaitingList"
            data-cy="remove"
          >
            Von Warteliste entfernen
          </button>
          <!--- Buchung stornieren -->
          <br /><br />
          <p v-if="settingsAccount?.booking?.status === 'PREBOOKED'">
            Falls Sie Ihre vorgemerkte Buchung stornieren möchten, können Sie dies hier tun.
          </p>
          <p v-else-if="settingsAccount?.booking?.status === 'CONFIRMED'">
            Falls Sie Ihre aktive Buchung stornieren möchten, können Sie dies hier tun.
          </p>
          <p v-else>Sie haben im Moment keine aktive Standbuchung.</p>
          <button
            type="button"
            class="btn btn-primary"
            :disabled="
              settingsAccount?.booking?.status == 'CANCELED' ||
              settingsAccount?.booking?.status == null ||
              settingsAccount?.booking?.status == 'BLOCKED'
            "
            @click="setCanceled"
            data-cy="cancel"
          >
            Standbuchung stornieren
          </button>
          <br /><br />
        </div>
        <!--- Konto löschen + Modal -->
        <p v-if="settingsAccount?.booking?.status === 'PREBOOKED'">
          Solange Sie eine vorgemerkte Standbuchung haben, können Sie Ihr Konto nicht löschen. Stonieren Sie Ihre Standbuchung, falls Sie
          Ihr Konto löschen wollen.
        </p>
        <p v-else-if="settingsAccount?.booking?.status === 'CONFIRMED'">
          Solange Sie eine bestätigte Standbuchung haben, können Sie Ihr Konto nicht löschen. Stonieren Sie Ihre Standbuchung, falls Sie Ihr
          Konto löschen wollen.
        </p>
        <p v-else-if="hasAnyAuthority('ROLE_ADMIN') && onlyOneAdmin">
          Sie sind der einzige Admin im System. Es ist nicht möglich, den einzigen Admin zu löschen.
        </p>
        <p v-else>Hier können Sie Ihr Konto löschen. Wenn Sie Ihr Konto löschen, kann es nicht wiederhergestellt werden.</p>
        <button
          type="button"
          class="btn btn-danger"
          id="show-btn"
          :disabled="
            settingsAccount?.booking?.status === 'PREBOOKED' ||
            settingsAccount?.booking?.status === 'CONFIRMED' ||
            (hasAnyAuthority('ROLE_ADMIN') && onlyOneAdmin)
          "
          @click="showModal"
        >
          Konto löschen
        </button>
        <b-modal ref="deleteAcc-modal" hide-footer title="Benutzerkonto löschen" @hidden="resetModal">
          <div class="d-block text-left">
            <div class="w-100">
              <b-alert show data-cy="deleteError" variant="danger" v-if="deleteError"
                ><strong>Ungültiges Passwort!</strong>
                Bitte überprüfen Sie Ihr Passwort und versuchen Sie es erneut.
              </b-alert>
            </div>
            <h4>Sind Sie sich sicher, dass Sie Ihr Konto löschen wollen?</h4>
            <p>Beachten Sie, dass dies nicht rückgängig gemacht werden kann.</p>
            <form name="deleteForm" id="delete-form" @submit.prevent="confirmDelete(settingsAccount.user.id)">
              <b-form-group label="Bestätigen Sie mit Ihrem Passwort." label-for="passwordConfirm">
                <b-form-input
                  id="passwordConfirm"
                  type="password"
                  name="passwordConfirm"
                  :class="{ valid: !v$.deleteAccount.passwordConfirm.$invalid, invalid: v$.deleteAccount.passwordConfirm.$invalid }"
                  placeholder="Ihr Passwort"
                  v-model="passwordConfirm"
                  data-cy="passwordconfirm"
                >
                </b-form-input>
              </b-form-group>
            </form>
          </div>
          <div class="d-flex justify-content-end">
            <b-button class="btn btn-secondary" @click="hideModal">Abbrechen</b-button>
            <b-button type="submit" class="btn btn-danger ml-3" id="confirmDelete" @click="confirmDelete(settingsAccount.user.id)"
              >Konto löschen</b-button
            >
          </div>
        </b-modal>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./settings.component.ts"></script>
