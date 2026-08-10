export interface GeoapifyProperties {
  state_code?: string;
  county_code?: string;

  country: string;
  country_code: string;
  state: string;

  county?: string;
  city?: string;
  postcode?: string;
  street?: string;
  housenumber?: string;

  formatted: string;
  lat: number;
  lon: number;
}