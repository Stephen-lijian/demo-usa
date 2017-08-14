package com.example.api.base.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Deprecated
public abstract class AppRequest extends AppHeader {

}
