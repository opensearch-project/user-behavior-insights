/**
ubi.js is downloaded from https://github.com/opensearch-project/user-behavior-insights/tree/main/ubi-javascript-collector.
*/

import axios from 'axios';

/**
 * Methods and client to talk directly with the OpenSearch UBI plugin
 * for logging events
 */
export class UbiClient {
    constructor(baseUrl) {
        // point to the specific middleware endpoint for receiving events
        this.url = `${baseUrl}/ubi_events`;
        
        this.rest_config = {
            headers: {
                'Content-type': 'application/json',
                'Accept': 'application/json'
            }
        };

        this.rest_client = axios.create({
            baseURL: baseUrl,
            headers: { 
                'Content-type': 'application/json', 
                'Accept': 'application/json' 
            },
            withCredentials: false
        });

        this.verbose = 0; // Default value for verbose
    }

    async trackEvent(e, message = null, message_type = null) {
        if (message) {
            if (e.message) {
                e['extra_info'] = message;
                if (message_type) {
                    e['extra_info_type'] = message_type;
                }
            } else {
                e.message = message;
                e.message_type = message_type;
            }
        }

        // Data prepper wants an array of JSON.
        let json = JSON.stringify([e]);
        if (this.verbose > 0) {
            console.log('POSTing event: ' + json);
        }

        return this._post(json);
    }

    async _post(data) {
        try {
            const response = await this.rest_client.post(this.url, data, this.rest_config);
            return response.data;
        } catch (error) {
            console.error(error);
        }
    }
}

export class UbiEvent {
  /**
   * This maps to the UBI Specification at https://github.com/o19s/ubi
   */
  constructor(action_name, client_id, session_id, query_id, event_attributes, message = null) {
    this.application = "Chorus"
    this.action_name = action_name;
    this.query_id = query_id;
    this.session_id = session_id;        
    this.client_id = client_id;
    this.user_id = '';
    this.timestamp = new Date().toISOString();
    this.message_type = 'INFO';
    this.message = message || '';     // Default to an empty string if no message
    this.event_attributes = event_attributes
  }

  setMessage(message, message_type = 'INFO') {
    this.message = message;
    this.message_type = message_type;
  }

  /**
   * Use to suppress null objects in the json output
   * @param key 
   * @param value 
   * @returns 
   */
  static replacer(key, value) {
    return (value == null) ? undefined : value; // Return undefined for null values
  }

  /**
   * 
   * @returns json string
   */
  toJson() {
    return JSON.stringify(this, UbiEvent.replacer);
  }
}

export class UbiEventAttributes {
  constructor(idField, id = null, description = null, details = null) {
    this.object = {
      object_id: id,
      object_id_field: idField,
      description: description,
      
    }
    
    // merge in the details, but make sure to filter out any defined properties
    // since details is a free form structure that could be anything.  
    var { object_id, object_id_field, description, ...filteredDetails } = details;
    
    this.object = { ...this.object, ...filteredDetails };
   
  }
}
