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

        <div class="alert alert-success" role="alert" v-if="success"><strong>Einstellungen wurden gespeichert!</strong></div>

        <div class="alert alert-danger" role="alert" v-if="errorEmailExists">
          <strong>E-Mail-Adresse wird bereits verwendet!</strong> Bitte wählen Sie eine andere aus.
        </div>

        <form name="form" id="settings-form" role="form" @submit.prevent="save()" v-if="settingsAccount" novalidate>
          <!--- Name des Unternehmens -->
          <div class="form-group">
            <label class="form-control-label" for="companyName">Firmenname</label>
            <input
              type="text"
              class="form-control"
              id="companyName"
              name="companyName"
              placeholder="Name Ihres Unternehmens"
              :class="{ valid: !v$.settingsAccount.companyName.$invalid, invalid: v$.settingsAccount.companyName.$invalid }"
              v-model="v$.settingsAccount.companyName.$model"
              minlength="1"
              maxlength="100"
              required
              data-cy="companyname"
            />
            <div v-if="v$.settingsAccount.companyName.$anyDirty && v$.settingsAccount.companyName.$invalid">
              <small class="form-text text-danger" v-if="!v$.settingsAccount.companyName.required">Ein Firmenname wird benötigt.</small>
              <small class="form-text text-danger" v-if="!v$.settingsAccount.companyName.minLength"
                >Ihr Firmenname muss mindestens 1 Zeichen lang sein.</small
              >
              <small class="form-text text-danger" v-if="!v$.settingsAccount.companyName.maxLength"
                >Ihr Firmenname darf nicht länger als 100 Zeichen sein.</small
              >
            </div>
          </div>
          <!--- Adresse des Unternehmens -->
          <div class="form-group">
            <label class="form-control-label" for="billingAddress">Rechnungsanschrift</label>
            <textarea
              id="billingAddress"
              name="billingAddress"
              class="form-control"
              placeholder="Rechnungsanschrift Ihres Unternehmens"
              :class="{ valid: !v$.settingsAccount.billingAddress.$invalid, invalid: v$.settingsAccount.billingAddress.$invalid }"
              v-model="v$.settingsAccount.billingAddress.$model"
              minlength="1"
              maxlength="254"
              required
              data-cy="billingaddress"
            >
            </textarea>
            <div v-if="v$.settingsAccount.billingAddress.$anyDirty && v$.settingsAccount.billingAddress.$invalid">
              <small class="form-text text-danger" v-if="!v$.settingsAccount.billingAddress.required"
                >Eine Rechnungsanschrift wird benötigt.</small
              >
              <small class="form-text text-danger" v-if="!v$.settingsAccount.billingAddress.minLength"
                >Ihre Rechnungsanschrift muss mindestens 1 Zeichen lang sein.</small
              >
              <small class="form-text text-danger" v-if="!v$.settingsAccount.billingAddress.maxLength"
                >Ihre Rechnungsanschrift darf nicht länger als 254 Zeichen sein.</small
              >
            </div>
          </div>
          <!--- Vorname des Ansprechpartners / Accountbesitzers -->
          <div class="form-group">
            <label class="form-control-label" for="firstName">Vorname Ansprechpartner</label>
            <input
              type="text"
              class="form-control"
              id="firstName"
              name="firstName"
              placeholder="Ihr Vorname"
              :class="{ valid: !v$.settingsAccount.firstName.$invalid, invalid: v$.settingsAccount.firstName.$invalid }"
              v-model="v$.settingsAccount.firstName.$model"
              minlength="1"
              maxlength="50"
              required
              data-cy="firstname"
            />
            <div v-if="v$.settingsAccount.firstName.$anyDirty && v$.settingsAccount.firstName.$invalid">
              <small class="form-text text-danger" v-if="!v$.settingsAccount.firstName.required">Ihr Vorname wird benötigt.</small>
              <small class="form-text text-danger" v-if="!v$.settingsAccount.firstName.minLength">
                Ihr Vorname muss mindestens 1 Zeichen lang sein</small
              >
              <small class="form-text text-danger" v-if="!v$.settingsAccount.firstName.maxLength">
                Ihr Vorname darf nicht länger als 50 Zeichen sein</small
              >
            </div>
          </div>
          <!--- Nachname des Ansprechpartners / Accountbesitzers -->
          <div class="form-group">
            <label class="form-control-label" for="lastName">Nachname Ansprechpartner</label>
            <input
              type="text"
              class="form-control"
              id="lastName"
              name="lastName"
              placeholder="Ihr Nachname"
              :class="{ valid: !v$.settingsAccount.lastName.$invalid, invalid: v$.settingsAccount.lastName.$invalid }"
              v-model="v$.settingsAccount.lastName.$model"
              minlength="1"
              maxlength="50"
              required
              data-cy="lastname"
            />
            <div v-if="v$.settingsAccount.lastName.$anyDirty && v$.settingsAccount.lastName.$invalid">
              <small class="form-text text-danger" v-if="!v$.settingsAccount.lastName.required">Ihr Nachname wird benötigt.</small>
              <small class="form-text text-danger" v-if="!v$.settingsAccount.lastName.minLength">
                Ihr Nachname muss mindestens 1 Zeichen lang sein</small
              >
              <small class="form-text text-danger" v-if="!v$.settingsAccount.lastName.maxLength">
                Ihr Nachname darf nicht länger als 50 Zeichen sein</small
              >
            </div>
          </div>
          <!--- E-Mail-Adresse des Ansprechpartners -->
          <div class="form-group">
            <label class="form-control-label" for="email">E-Mail-Adresse</label>
            <input
              type="email"
              class="form-control"
              id="email"
              name="email"
              placeholder="Ihre E-Mail-Adresse"
              :class="{ valid: !v$.settingsAccount.email.$invalid, invalid: v$.settingsAccount.email.$invalid }"
              v-model="v$.settingsAccount.email.$model"
              minlength="5"
              maxlength="254"
              email
              required
              data-cy="email"
            />
            <div v-if="v$.settingsAccount.email.$anyDirty && v$.settingsAccount.email.$invalid">
              <small class="form-text text-danger" v-if="!v$.settingsAccount.email.required">Ihre E-Mail-Adresse wird benötigt.</small>
              <small class="form-text text-danger" v-if="!v$.settingsAccount.email.email">Ihre E-Mail-Adresse ist ungültig.</small>
              <small class="form-text text-danger" v-if="!v$.settingsAccount.email.minLength">
                Ihre E-Mail-Adresse muss mindestens 5 Zeichen lang sein</small
              >
              <small class="form-text text-danger" v-if="!v$.settingsAccount.email.maxLength">
                Ihre E-Mail-Adresse darf nicht länger als 50 Zeichen sein</small
              >
            </div>
          </div>
          <!--- Kurzbeschreibung des Unternehmens -->
          <div class="form-group">
            <label class="form-control-label" for="description">Kurze Beschreibung Ihres Unternehmens (max. 254 Zeichen)</label>
            <textarea
              id="description"
              name="description"
              class="form-control"
              placeholder="Beschreibung Ihres Unternehmens"
              :class="{ valid: !v$.settingsAccount.description.$invalid, invalid: v$.settingsAccount.description.$invalid }"
              v-model="v$.settingsAccount.description.$model"
              minlength="0"
              maxlength="254"
              data-cy="description"
            ></textarea>
            <div v-if="v$.settingsAccount.description.$invalid">
              <small class="form-text text-danger" v-if="v$.settingsAccount.description.maxLength">
                Ihre Firmenbeschreibung darf nicht länger als 254 Zeichen sein.
              </small>
            </div>
          </div>
          <!--- Unternehmenslogo hochladen / aktualisieren -->
          <div class="form-group">
            <label class="form-control-label" for="logoUpload">Laden Sie Ihr Unternehmenslogo hoch</label>
            <input
              type="file"
              ref="file"
              class="form-control-file"
              id="logoUpload"
              name="logoUpload"
              :class="{ valid: !v$.settingsAccount.logoUpload.$invalid, invalid: v$.settingsAccount.logoUpload.$invalid }"
              data-cy="logoupload"
              accept="image/*"
              @change="previewImage"
            />
            <!--- Unternehmenslogo Previewfenster -->
            <div class="form-group">
              <template v-if="preview">
                <img :src="preview" class="img-fluid" style="height: 150px; width: auto" />
              </template>
            </div>
          </div>
          <!--- Checkbox um Freigabe in Ausstellerliste zu widerrufen -->
          <div class="form-group">
            <label class="form-control-label" for="exhibitorList">Hier können Sie Ihre Freigabe in der Austellerliste verwalten</label>
            <br />
            <input
              type="checkbox"
              id="exhibitorList"
              name="exhibitorList"
              :class="{ valid: !v$.settingsAccount.exhibitorList.$invalid, invalid: v$.settingsAccount.exhibitorList.$invalid }"
              v-model="v$.settingsAccount.exhibitorList.$model"
              data-cy="exhibitorlist"
            />
            {{ isChecked }}
          </div>
          <button type="submit" :disabled="v$.settingsAccount.$invalid" class="btn btn-primary" data-cy="submit">Speichern</button>
        </form>
        <br />
        <!--- Aktueller Buchungsstaus -->
        <div>
          <p v-if="settingsAccount.status">
            Ihr aktueller Buchungsstatus: <strong>{{ settingsAccount.status }}</strong>
          </p>
          <p v-else>Ihr aktueller Buchungsstaus: <strong>Keine Buchung vorhanden</strong></p>
        </div>
        <!--- Standbuchung bestätigen -->
        <p v-if="settingsAccount.status === 'PREBOOKED'">Sie können Ihre vorgemerkte Standbuchung hier bestätigen.</p>
        <p v-else>Sie haben im Moment keine vorgemerkte Buchung</p>
        <button
          type="button"
          class="btn btn-primary"
          :disabled="settingsAccount.status != 'PREBOOKED'"
          @click="confirmBooking"
          data-cy="confirmbooking"
        >
          Standbuchung bestätigen
        </button>
        <br /><br />
        <!--- Von Warteliste entfernen -->
        <p v-if="settingsAccount.waitingList">
          Sie befinden sich aktuell auf der Warteliste. Wenn Sie sich davon entfernen möchten, nutzen Sie den folgenden Button.
        </p>
        <p v-else>
          Sie befinden sich im Moment nicht auf der Warteliste.<br />
          Falls Sie sich zur Warteliste hinzufügen wollen, können Sie dies unter "Ihre Buchungen (oder wie auch immer wir das nennen)" tun.
        </p>
        <button type="button" class="btn btn-primary" :disabled="!settingsAccount.waitingList" @click="removeWaitingList" data-cy="remove">
          Von Warteliste entfernen
        </button>
        <!--- Buchung stornieren -->
        <br /><br />
        <p v-if="settingsAccount.status === 'PREBOOKED'">
          Falls Sie Ihre vorgemerkte Buchung stornieren möchten, können Sie dies hier tun.
        </p>
        <p v-else-if="settingsAccount.status === 'CONFIRMED'">
          Falls Sie Ihre aktive Buchung stornieren möchten, können Sie dies hier tun.
        </p>
        <p v-else>Sie haben im Moment keine aktive Standbuchung.</p>
        <button
          type="button"
          class="btn btn-primary"
          :disabled="settingsAccount.status == 'CANCELED' || settingsAccount.status == null || settingsAccount.status == 'BLOCKED'"
          @click="setCanceled"
          data-cy="cancel"
        >
          Standbuchung stornieren
        </button>
        <!--- Konto löschen + Modal -->
        <br /><br />
        <p>
          Hier können Sie Ihr Konto löschen. Dies kann nicht rückgängig gemacht werden. Beachten Sie, dass dies nicht möglich ist, wenn Sie
          eine aktive Standbuchung haben.
        </p>
        <button type="button" class="btn btn-danger" id="show-btn" @click="showModal">Konto löschen</button>
        <b-modal ref="deleteAcc-modal" hide-footer title="Benutzerkonto löschen" @hidden="resetModal">
          <div class="d-block text-left">
            <div class="w-100">
              <b-alert show data-cy="deleteError" variant="danger" v-if="deleteError"
                ><strong>Ungültiges Passwort!</strong> Bitte überprüfen Sie Ihr Passwort und versuchen Sie es erneut.
              </b-alert>
            </div>
            <h4>Sind Sie sich sicher, dass Sie Ihr Konto löschen wollen?</h4>
            <p>Beachten Sie, dass dies nicht rückgängig gemacht werden kann.</p>
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
          </div>
          <div class="d-flex justify-content-end">
            <b-button class="btn btn-secondary" @click="hideModal">Abbrechen</b-button>
            <b-button class="btn btn-danger ml-3" id="confirmDelete" @click="confirmDelete">Konto löschen</b-button>
          </div>
        </b-modal>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./settings.component.ts"></script>
