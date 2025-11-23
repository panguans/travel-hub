import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Agency e2e test', () => {
  const agencyPageUrl = '/agency';
  const agencyPageUrlPattern = new RegExp('/agency(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const agencySample = {};

  let agency;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"W3","firstName":"Ashton","lastName":"Grady","email":"Jamal91@yahoo.com","imageUrl":"aha","langKey":"writhing g"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/agencies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/agencies').as('postEntityRequest');
    cy.intercept('DELETE', '/api/agencies/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (agency) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/agencies/${agency.id}`,
      }).then(() => {
        agency = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('Agencies menu should load Agencies page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('agency');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Agency').should('exist');
    cy.url().should('match', agencyPageUrlPattern);
  });

  describe('Agency page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(agencyPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Agency page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/agency/new$'));
        cy.getEntityCreateUpdateHeading('Agency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agencyPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/agencies',
          body: {
            ...agencySample,
            user_agency: user,
          },
        }).then(({ body }) => {
          agency = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/agencies+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/agencies?page=0&size=20>; rel="last",<http://localhost/api/agencies?page=0&size=20>; rel="first"',
              },
              body: [agency],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(agencyPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(agencyPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Agency page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('agency');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agencyPageUrlPattern);
      });

      it('edit button click should load edit Agency page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Agency');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agencyPageUrlPattern);
      });

      it('edit button click should load edit Agency page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Agency');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agencyPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Agency', () => {
        cy.intercept('GET', '/api/agencies/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('agency').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agencyPageUrlPattern);

        agency = undefined;
      });
    });
  });

  describe('new Agency page', () => {
    beforeEach(() => {
      cy.visit(`${agencyPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Agency');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Agency', () => {
      cy.get(`[data-cy="description"]`).type('exacerbate geez');
      cy.get(`[data-cy="description"]`).should('have.value', 'exacerbate geez');

      cy.get(`[data-cy="address"]`).type('guest nicely');
      cy.get(`[data-cy="address"]`).should('have.value', 'guest nicely');

      cy.get(`[data-cy="website"]`).type('brand mundane');
      cy.get(`[data-cy="website"]`).should('have.value', 'brand mundane');

      cy.get(`[data-cy="ratingAvg"]`).type('18354.4');
      cy.get(`[data-cy="ratingAvg"]`).should('have.value', '18354.4');

      cy.get(`[data-cy="user_agency"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        agency = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', agencyPageUrlPattern);
    });
  });
});
