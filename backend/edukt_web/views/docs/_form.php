<?php

use yii\helpers\Html;
use yii\widgets\ActiveForm;
use dosamigos\fileupload\FileUploadUI;
use yii\helpers\ArrayHelper;
use app\models\Users;
use dosamigos\fileinput\FileInput;


/* @var $this yii\web\View */
/* @var $model app\models\Docs */
/* @var $form yii\widgets\ActiveForm */
?>

<div class="docs-form">

    <?php $form = ActiveForm::begin(); ?>

    <?= $form->field($model, 'nombre')->textInput(['maxlength' => 100]) ?>

    <?= $form->field($model, 'tipo')->textInput(['maxlength' => 50]) ?>

    <?= $form->field($model, 'url_doc')->widget(\dosamigos\fileinput\BootstrapFileInput::className(), [
      'options' => ['accept' => '*', 'multiple' => False],
      'clientOptions' => [
        'previewFileType' => 'text',
        'browseClass' => 'btn btn-success',
        'uploadClass' => 'btn btn-info',
        'removeClass' => 'btn btn-danger',
        'removeIcon' => '<i class="glyphicon glyphicon-trash"></i> '
        ]
        ]);?>

    <?= $form->field($model, 'descripcion')->textInput(['maxlength' => 250]) ?>

    <?= $form->field($model, 'img_url')->textInput(['maxlength' => 250]) ?>

    <?= $form->field($model, 'users_id')->dropDownList(ArrayHelper::map(users::find()->where( array('tipo_user'=>'Profesor'))->all(),'uid','email'),
    ['prompt'=>'Seleccione'])
    ?>

    <div class="form-group">
        <?= Html::submitButton($model->isNewRecord ? 'Create' : 'Update', ['class' => $model->isNewRecord ? 'btn btn-success' : 'btn btn-primary']) ?>
    </div>

    <?php ActiveForm::end(); ?>

</div>
